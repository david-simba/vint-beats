package com.davidsimba.vintbeats.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
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
import com.davidsimba.vintbeats.feature.search.ui.SearchScreen
import com.davidsimba.vintbeats.shared.components.background.Background
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
        contentWindowInsets = WindowInsets(0),
        bottomBar = {
            if (showBottomBar) BottomNavBar(navController)
        }
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize()) {
            Background(modifier = Modifier.fillMaxSize())
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
                composable(Screen.Search.route) { SearchScreen() }
                composable(Screen.Profile.route) { }
            }
        }
    }
}