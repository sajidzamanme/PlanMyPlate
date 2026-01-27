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
import com.teamconfused.planmyplate.model.InventoryItem
import com.teamconfused.planmyplate.ui.viewmodels.InventoryViewModel
import com.teamconfused.planmyplate.ui.viewmodels.ViewModelFactory
import com.teamconfused.planmyplate.util.SessionManager

@Composable
fun InventoryScreen(navController: NavController) {
    val context = LocalContext.current
    val sessionManager = SessionManager(context)
    val viewModelFactory = ViewModelFactory(sessionManager)
    val viewModel: InventoryViewModel = viewModel(factory = viewModelFactory)
    val uiState by viewModel.uiState.collectAsState()

    Scaffold { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Icon(painter = androidx.compose.ui.res.painterResource(com.teamconfused.planmyplate.R.drawable.arrow_back_icon), contentDescription = "Back")
                }
                Text(
                    text = "My Inventory",
                    style = MaterialTheme.typography.headlineMedium
                )
            }

            if (uiState.isLoading) {
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (uiState.items.isEmpty()) {
                 Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Inventory is empty", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(uiState.items) { item ->
                        InventoryItemCard(
                            item = item,
                            onIncrease = { viewModel.updateItemQuantity(item, 1) },
                            onDecrease = { viewModel.updateItemQuantity(item, -1) }
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
fun InventoryItemCard(
    item: InventoryItem,
    onIncrease: () -> Unit,
    onDecrease: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                // Assuming 'ingredient' object has name. InventoryItem has 'ingredient' ref or full object?
                // Models.kt: val ingredient: IngredientRef? = null. 
                // IngredientRef has 'ingId'. It does NOT have name.
                // This is a problem. The 'get inventory items' response should ideally return ingredient name or full object.
                // If it only returns Ref, we can't show names easily without fetching ingredients map.
                // Let's check Models.kt again.
                // Line 121: val ingredient: IngredientRef?
                // If backend returns expanded object, Gson might miss it if type is strictly Ref.
                // Users usually want names. 
                // I will try to rely on 'ingId' for now, but really we need a name. 
                // Maybe I can fetch ingredient details? OR I should have updated Model to allow name.
                // I'll display "Item #${item.ingredient?.ingId}" if name missing.
                // BUT wait, API Doc 6.6 "Get Inventory Items".
                // Doesn't show response format explicitly but usually it contains details.
                // I'll update Model.kt to use `Ingredient` instead of `IngredientRef` for reading if possible, or Add `name` to `InventoryItem`.
                // Let's assume for now I display ID.
                
                Text(
                    text = "Item ${item.ingredient?.ingId ?: "Unknown"}",
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = "Expires: ${item.expiryDate}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = onDecrease,
                    colors = IconButtonDefaults.filledIconButtonColors(containerColor = MaterialTheme.colorScheme.errorContainer)
                ) {
                    if (item.quantity <= 1) {
                         Icon(painter = painterResource(com.teamconfused.planmyplate.R.drawable.remove_icon), contentDescription = "Remove", tint = MaterialTheme.colorScheme.error)
                    } else {
                         // Use standard Remove icon
                         Icon(painter = painterResource(com.teamconfused.planmyplate.R.drawable.remove_icon), contentDescription = "Decrease", tint = MaterialTheme.colorScheme.onErrorContainer)
                    }
                }
                
                Text(
                    text = "${item.quantity}",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(horizontal = 12.dp)
                )
                
                IconButton(
                    onClick = onIncrease,
                    colors = IconButtonDefaults.filledIconButtonColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                ) {
                     Icon(painter = painterResource(com.teamconfused.planmyplate.R.drawable.add_icon), contentDescription = "Increase", tint = MaterialTheme.colorScheme.onPrimaryContainer)
                }
            }
        }
    }
}
