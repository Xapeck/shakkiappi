package com.example.REMOVED

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.REMOVED.presentation.ui.screens.MainClockScreen
import com.example.REMOVED.presentation.ui.screens.SettingsScreen
import com.example.REMOVED.presentation.ui.screens.StatsScreen
import com.example.REMOVED.presentation.ui.theme.ShakkiappiTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ShakkiappiTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    val navController = rememberNavController()
                    NavHost(navController, startDestination = "main_clock") {
                        composable("main_clock") {
                            MainClockScreen(
                                onNavigateToSettings = { navController.navigate("settings") },
                                onNavigateToStats = { navController.navigate("stats") }
                            )
                        }
                        composable("settings") {
                            SettingsScreen(onNavigateBack = { navController.popBackStack() })
                        }
                        composable("stats") {
                            StatsScreen(onNavigateBack = { navController.popBackStack() })
                        }
                    }
                }
            }
        }
    }
}
