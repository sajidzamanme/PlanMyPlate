package com.teamconfused.planmyplate.ui.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed class Screen {
    @Serializable
    data object Welcome : Screen()

    @Serializable
    data object Login : Screen()

    @Serializable
    data object Main : Screen()

    @Serializable
    data object Signup : Screen()

    @Serializable
    data object ForgotPassword : Screen()

    @Serializable
    data object PreferenceSelection : Screen()
}
