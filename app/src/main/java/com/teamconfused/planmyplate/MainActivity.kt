package com.teamconfused.planmyplate

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.rememberNavController
import com.teamconfused.planmyplate.ui.navigation.NavGraph
import com.teamconfused.planmyplate.ui.theme.PlanMyPlateTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PlanMyPlateTheme {
                val navController = rememberNavController()
                NavGraph(navController = navController)
            }
        }
    }
}
