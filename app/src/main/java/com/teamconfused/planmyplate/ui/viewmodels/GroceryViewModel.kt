package com.teamconfused.planmyplate.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.teamconfused.planmyplate.model.GroceryList
import com.teamconfused.planmyplate.model.GroceryListItem
import com.teamconfused.planmyplate.model.PurchaseItemsRequest
import com.teamconfused.planmyplate.network.GroceryListService
import com.teamconfused.planmyplate.util.SessionManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class GroceryUiState(
    val groceryLists: List<GroceryList> = emptyList(),
    val activeListItems: List<GroceryListItem> = emptyList(),
    val checkedItems: Set<Int> = emptySet(), // IDs of items checked for purchase
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val activeListId: Int? = null
)

class GroceryViewModel(
    private val groceryListService: GroceryListService,
    private val sessionManager: SessionManager
) : ViewModel() {

    private var updateJob: kotlinx.coroutines.Job? = null
    private val _uiState = MutableStateFlow(GroceryUiState())
    val uiState: StateFlow<GroceryUiState> = _uiState.asStateFlow()

    init {
        fetchGroceryLists()
    }

    fun fetchGroceryLists() {
        val userId = sessionManager.getUserId()
        if (userId == -1) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                // Fetch all lists
                val lists = groceryListService.getGroceryListsForUser(userId)
                
                // Active list logic
                val activeList = lists.find { it.status == "active" } ?: lists.firstOrNull()
                val items = activeList?.items ?: emptyList() // Use .items

                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        groceryLists = lists,
                        activeListId = activeList?.listId,
                        activeListItems = items 
                    ) 
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, errorMessage = e.message) }
            }
        }
    }
    
    // Toggle check status of an item
    fun toggleItemCheck(itemId: Int) {
        _uiState.update { 
            val current = it.checkedItems
            if (current.contains(itemId)) {
                it.copy(checkedItems = current - itemId)
            } else {
                it.copy(checkedItems = current + itemId)
            }
        }
    }


    
    fun updateListQuantity(item: GroceryListItem, delta: Int) {
        val currentQty = item.quantity ?: 1
        val newQty = (currentQty + delta).coerceAtLeast(1)
        
        if (newQty == currentQty) return

        // 1. Optimistic Local Update
        _uiState.update { state ->
            val updatedItems = state.activeListItems.map { listItem ->
                if (listItem.id == item.id) listItem.copy(quantity = newQty) else listItem
            }
            state.copy(activeListItems = updatedItems)
        }

        // 2. Debounced API Call
        updateJob?.cancel()
        updateJob = viewModelScope.launch {
            kotlinx.coroutines.delay(500) // Debounce 500ms
            try {
                _uiState.value.activeListId?.let { listId ->
                    val itemId = item.id ?: return@let
                    // Note: sending { "quantity": newQty }
                    val req = mapOf("quantity" to newQty)
                    groceryListService.updateGroceryListItem(listId, itemId, req)
                }
            } catch (e: Exception) {
                // Ignore 404 if endpoint doesn't exist yet, or handle error
                // Ideally revert local state if failed, but for better UX we might just log
                println("Failed to sync quantity: ${e.message}")
            }
        }
    }

    fun purchaseSelectedItems(onSuccess: () -> Unit) {
        val listId = _uiState.value.activeListId ?: return
        // Checked items track itemId (row ID). Purchase API likely expects ingredientIds based on old doc, 
        // BUT user documentation update in prompt removed "URL" and "Request Body" section for Purchase but kept "Action".
        // Wait, user provided snippet has "URL: /grocery-lists/{id}/purchase" and "Request Body: { "ingredientIds": ... }".
        // So we need to map checked itemIds (row IDs) to ingredientIds.
        
        val checkedIds = _uiState.value.checkedItems
        if (checkedIds.isEmpty()) return
        
        // Find corresponding ingredients
        val ingredientIds = _uiState.value.activeListItems
            .filter { checkedIds.contains(it.id) }
            .mapNotNull { it.ingredient?.ingId }
            
        if (ingredientIds.isEmpty()) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            try {
                // Send selected ingredient IDs to the API
                val request = PurchaseItemsRequest(ingredientIds = ingredientIds)
                val response = groceryListService.purchaseItems(listId, request)
                
                if (response.isSuccessful) {
                    _uiState.update { 
                        it.copy(
                            isLoading = false, 
                            checkedItems = emptySet()
                            // Optimistically remove items or re-fetch
                        ) 
                    }
                    fetchGroceryLists() // Refresh to see reduced list
                    onSuccess()
                } else {
                    _uiState.update { it.copy(isLoading = false, errorMessage = "Purchase failed") }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, errorMessage = e.message) }
            }
        }
    }
}
