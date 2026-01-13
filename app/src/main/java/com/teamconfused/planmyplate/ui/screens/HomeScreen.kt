package com.teamconfused.planmyplate.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.teamconfused.planmyplate.model.Recipe
import com.teamconfused.planmyplate.ui.components.CategorizedRecipeSection

@Composable
fun HomeScreen(navController: NavController) {
    val recipes = listOf<Recipe>(
        Recipe("Recipe 1", "Description 1", 10.0, listOf(), "https://images.immediate.co.uk/production/volatile/sites/30/2020/08/chorizo-mozarella-gnocchi-bake-cropped-9ab73a3.jpg?quality=90&webp=true&resize=440,400"),
        Recipe("Recipe 2", "Description 2", 20.0, listOf(), "https://images.immediate.co.uk/production/volatile/sites/2/2024/12/11.19.24OliveJanIssueChickenBake019preview-d3eedda.jpg"),
        Recipe("Recipe 3", "Description 3", 30.0, listOf(), "https://www.allrecipes.com/thmb/aF6uJ6oDIFIazy2pdQC0kdGDgp8=/1500x0/filters:no_upscale():max_bytes(150000):strip_icc()/8421914-Marry-Me-Chicken-Soup-4x3-179-843abc8af99247dcadb3f79a91681d49.jpg"),
        Recipe("Recipe 4", "Description 4", 40.0, listOf(), "https://www.tasteofhome.com/wp-content/uploads/2025/06/30-Quick-Instant-Pot-Recipes_TOHD24_72936_SarahTramonte_4_FT.jpg"),
        Recipe("Recipe 5", "Description 5", 50.0, listOf(), "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcRXNGEnJO0Cy7SSTrRybtsR6Gt3Fq1yNOpY9g&s"),
    )

    Column(
        modifier = Modifier.verticalScroll(rememberScrollState())
    ) {
        CategorizedRecipeSection(
            "Test Section",
            recipes,
            {},
            {}
        )

        CategorizedRecipeSection(
            "Test Section",
            recipes,
            {},
            {}
        )

        CategorizedRecipeSection(
            "Test Section",
            recipes,
            {},
            {}
        )

        CategorizedRecipeSection(
            "Test Section",
            recipes,
            {},
            {}
        )
    }

}
