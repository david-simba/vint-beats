package com.davidsimba.vintbeats.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import com.davidsimba.vintbeats.feature.cassette.ui.CassetteSharedViewModel
import com.davidsimba.vintbeats.feature.cassette.ui.CustomizeCassetteScreen
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
                composable(Screen.Home.route) {
                    HomeScreen(
                        onCreateCassette = { navController.navigate(Screen.Search.route) }
                    )
                }
                composable(Screen.Search.route) { entry ->
                    val sharedViewModel: CassetteSharedViewModel = hiltViewModel(
                        remember(entry) { navController.getBackStackEntry(Screen.Search.route) }
                    )
                    SearchScreen(
                        onTrackSelected = { track ->
                            sharedViewModel.selectTrack(track)
                            navController.navigate(Screen.CustomizeCassette.route)
                        }
                    )
                }
                composable(Screen.CustomizeCassette.route) { entry ->
                    val searchEntry = remember(entry) { navController.getBackStackEntry(Screen.Search.route) }
                    val sharedViewModel: CassetteSharedViewModel = hiltViewModel(searchEntry)
                    CustomizeCassetteScreen(
                        viewModel = sharedViewModel,
                        onBack = { navController.popBackStack() },
                        onSave = {
                            navController.navigate(Screen.Home.route) {
                                popUpTo(Screen.Home.route) { inclusive = true }
                            }
                        }
                    )
                }
                composable(Screen.Profile.route) { }
            }
        }
    }
}
