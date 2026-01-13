package com.teamconfused.planmyplate.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.teamconfused.planmyplate.model.Recipe

@Composable
fun RecipeCard(recipe: Recipe, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .width(150.dp)
            .clickable(onClick = onClick)
    ) {
        Column {
            AsyncImage(
                model = recipe.imageUrl,
                contentDescription = recipe.title,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f),
                contentScale = ContentScale.Crop
            )
            Text(
                text = recipe.title,
                modifier = Modifier.padding(start = 8.dp, end = 8.dp, top = 8.dp)
            )
            Text(
                text = "$${recipe.price}",
                modifier = Modifier.padding(start = 8.dp, end = 8.dp, bottom = 8.dp)
            )
        }
    }
}
