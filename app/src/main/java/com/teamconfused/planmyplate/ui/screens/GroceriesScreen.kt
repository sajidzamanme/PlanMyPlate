package com.teamconfused.planmyplate.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.teamconfused.planmyplate.R
import com.teamconfused.planmyplate.model.GroceryListItem
import com.teamconfused.planmyplate.ui.viewmodels.GroceryViewModel
import com.teamconfused.planmyplate.ui.viewmodels.ViewModelFactory
import com.teamconfused.planmyplate.util.SessionManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GroceriesScreen(navController: NavController) {
    val context = LocalContext.current
    val sessionManager = SessionManager(context)
    val viewModelFactory = ViewModelFactory(sessionManager)
    val viewModel: GroceryViewModel = viewModel(factory = viewModelFactory)
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        floatingActionButton = {
            if (uiState.checkedItems.isNotEmpty()) {
                FloatingActionButton(
                    onClick = { viewModel.purchaseSelectedItems { /* Optional toast or navigation */ } }
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(painter = painterResource(R.drawable.shopping_icon), contentDescription = "Purchase")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Add to Inventory (${uiState.checkedItems.size})")
                    }
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Grocery List",
                    style = MaterialTheme.typography.headlineMedium
                )
                TextButton(onClick = { navController.navigate("inventory") }) {
                    Text("My Inventory")
                }
            }

            if (uiState.isLoading) {
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (uiState.activeListItems.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Your grocery list is empty", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                   items(uiState.activeListItems) { item ->
                       GroceryItemCard(
                           item = item,
                           isChecked = uiState.checkedItems.contains(item.ingredientId ?: item.id ?: 0),
                           onToggle = { viewModel.toggleItemCheck(item.ingredientId ?: item.id ?: 0) }
                       )
                   }
                }
            }
            
            uiState.errorMessage?.let { msg ->
                Text(msg, color = MaterialTheme.colorScheme.error)
            }
        }
    }
}

@Composable
fun GroceryItemCard(item: GroceryListItem, isChecked: Boolean, onToggle: () -> Unit) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(checked = isChecked, onCheckedChange = { onToggle() })
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(text = item.itemName, style = MaterialTheme.typography.bodyLarge)
                item.quantity?.let { qty ->
                     Text(
                         text = "Qty: $qty ${item.unit ?: ""}",
                         style = MaterialTheme.typography.bodySmall,
                         color = MaterialTheme.colorScheme.onSurfaceVariant
                     )
                }
            }
            Spacer(modifier = Modifier.weight(1f))
            item.price?.let { price ->
                Text(text = "$${price}", style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}
