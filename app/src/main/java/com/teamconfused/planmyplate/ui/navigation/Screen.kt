package com.teamconfused.planmyplate.ui.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed class Screen {
    @Serializable
    data object Welcome : Screen()
    
    @Serializable
    data object Login : Screen()
    
    @Serializable
    data object Home : Screen()

    @Serializable
    data object Signup : Screen()
}
