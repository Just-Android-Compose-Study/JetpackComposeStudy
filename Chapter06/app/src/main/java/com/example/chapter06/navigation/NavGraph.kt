package com.example.chapter06.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.chapter06.screens.HomeScreen
import com.example.chapter06.screens.SplashScreen


@Composable
fun SetupNavGraph(navController: NavHostController) {
    NavHost(navController = navController, startDestination = Screens.splash) {
        composable(Screens.splash) {
            SplashScreen(navController = navController)
        }
        composable(Screens.home) {
            // 자체 NavHost 구성
            HomeScreen()
        }
    }
}