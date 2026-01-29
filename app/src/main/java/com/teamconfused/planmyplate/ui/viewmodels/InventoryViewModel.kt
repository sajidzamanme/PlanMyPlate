package com.teamconfused.planmyplate.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.teamconfused.planmyplate.model.Inventory
import com.teamconfused.planmyplate.model.InventoryItem
import com.teamconfused.planmyplate.network.InventoryService
import com.teamconfused.planmyplate.util.SessionManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class InventoryUiState(
    val inventory: Inventory? = null,
    val items: List<InventoryItem> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class InventoryViewModel(
    private val inventoryService: InventoryService,
    private val sessionManager: SessionManager
) : ViewModel() {

    private var updateJob: kotlinx.coroutines.Job? = null

    private val _uiState = MutableStateFlow(InventoryUiState())
    val uiState: StateFlow<InventoryUiState> = _uiState.asStateFlow()

    init {
        fetchInventory()
    }

    fun fetchInventory() {
        val userId = sessionManager.getUserId()
        if (userId == -1) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            try {
                // Fetch user logic
                // API 6.1 Get Inventory for User
                val inventory = inventoryService.getInventoryForUser(userId)
                
                // Then fetch items for that inventory
                // API 6.6 Get Inventory Items
                val items = inventory.id?.let { inventoryService.getInventoryItems(it) } ?: emptyList()
                
                _uiState.update { 
                    it.copy(
                        isLoading = false, 
                        inventory = inventory, 
                        items = items
                    ) 
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, errorMessage = e.message) }
            }
        }
    }
    
    fun updateItemQuantity(item: InventoryItem, delta: Int) {
        val inventoryId = _uiState.value.inventory?.id ?: return
        val currentQty = item.quantity
        val newQty = currentQty + delta
        
        if (newQty < 0) return 

        // Optimistic Local Update
        viewModelScope.launch {
             val updatedItems = _uiState.value.items.map {
                 if (it.id == item.id) it.copy(quantity = newQty) else it
             }.filter { it.quantity > 0 } // Remove locally if 0
             
             _uiState.update { it.copy(items = updatedItems) }

             // Logic Switch:
             // 0 -> Delete (Immediate)
             // > 0 -> Debounced Update (or Add if that's the only working endpoint)
             
             if (newQty == 0 && item.id != null) {
                 try {
                    inventoryService.removeItemFromInventory(item.id)
                 } catch (e: Exception) {
                     fetchInventory() // Revert on failure
                 }
                 return@launch
             }

             // Debounced Update
             updateJob?.cancel()
             updateJob = launch {
                 kotlinx.coroutines.delay(500)
                 try {
                     if (delta != 0 && item.id != null) {
                        // Use new UPDATE endpoint (requires backend impl)
                        val req = com.teamconfused.planmyplate.model.InventoryItemRequest(
                             quantity = newQty,
                             expiryDate = item.expiryDate,
                             ingredient = com.teamconfused.planmyplate.model.IngredientRef(ingId = item.ingredient?.ingId ?: 0)
                         )
                        inventoryService.updateInventoryItem(item.id, req)
                     }
                 } catch (e: Exception) {
                     // Fallback mechanism if Update fails? 
                     // Or just log.
                     println("Inventory sync failed: ${e.message}")
                 }
             }
        }
    }
}
