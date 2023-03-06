package com.example.chapter06

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.chapter06.screens.SplashScreen

class MainActivity: ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
            NavHost(navController = navController, startDestination = Screens.Splash) {
                composable(route = Screens.Splash) {
                    SplashScreen(navController = navController)
                }
                composable(route = Screens.Home) {
                    HomeScreen(navController = navController)
                }
            }
        }
    }
}