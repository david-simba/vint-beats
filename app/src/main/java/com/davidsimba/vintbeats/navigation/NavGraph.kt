package com.davidsimba.vintbeats.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import com.davidsimba.vintbeats.feature.auth.ui.AuthScreen
import com.davidsimba.vintbeats.feature.home.ui.HomeScreen
import com.davidsimba.vintbeats.shared.components.navbar.BottomNavBar

private val bottomNavRoutes = setOf(
    Screen.Home.route,
    Screen.Search.route,
    Screen.Profile.route
)

@Composable
fun NavGraph(
    navController: NavHostController,
    startDestination: String,
) {
    val currentEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentEntry?.destination?.route
    val showBottomBar = currentRoute in bottomNavRoutes

    Scaffold(
        bottomBar = {
            if (showBottomBar) BottomNavBar(navController)
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Auth.route) {
                AuthScreen(
                    onLoginSuccess = {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Auth.route) { inclusive = true }
                        }
                    }
                )
            }
            composable(Screen.Home.route) { HomeScreen() }
            composable(Screen.Search.route) { }
            composable(Screen.Profile.route) { }
        }
    }
}