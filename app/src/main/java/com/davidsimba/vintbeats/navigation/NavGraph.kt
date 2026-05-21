package com.davidsimba.vintbeats.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navArgument
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.davidsimba.vintbeats.feature.cassette.ui.CassetteSharedViewModel
import com.davidsimba.vintbeats.feature.cassette.ui.CustomizeCassetteScreen
import com.davidsimba.vintbeats.feature.home.ui.HomeScreen
import com.davidsimba.vintbeats.feature.library.ui.LibraryScreen
import com.davidsimba.vintbeats.feature.player.ui.PlaybackViewModel
import com.davidsimba.vintbeats.feature.player.ui.PlayerScreen
import com.davidsimba.vintbeats.feature.search.ui.SearchScreen
import com.davidsimba.vintbeats.shared.components.MiniPlayer
import com.davidsimba.vintbeats.shared.components.background.Background
import com.davidsimba.vintbeats.shared.components.navbar.BottomNavBar

private val bottomNavRoutes = setOf(
    Screen.Home.route,
    Screen.Search.route,
    Screen.Library.route
)

@Composable
fun NavGraph(
    navController: NavHostController,
    startDestination: String,
) {
    val currentEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentEntry?.destination?.route
    val showBottomBar = currentRoute in bottomNavRoutes

    val playbackViewModel: PlaybackViewModel = hiltViewModel()
    val currentCassette by playbackViewModel.currentCassette.collectAsStateWithLifecycle()
    val playbackState by playbackViewModel.playerState.collectAsStateWithLifecycle()

    val showMiniPlayer = currentCassette != null && currentRoute != Screen.Player.route

    Scaffold(
        contentWindowInsets = WindowInsets(0),
        bottomBar = {
            Column {
                if (showMiniPlayer) {
                    MiniPlayer(
                        cassette = currentCassette!!,
                        playerState = playbackState,
                        onTogglePlayPause = playbackViewModel::togglePlayPause,
                        onTap = {
                            navController.navigate(Screen.Player.route(currentCassette!!.id)) {
                                launchSingleTop = true
                            }
                        }
                    )
                }
                if (showBottomBar) BottomNavBar(navController)
            }
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
                        onCreateCassette = {
                            navController.navigate(Screen.Search.route) {
                                launchSingleTop = true
                            }
                        }
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
                            navController.navigate(Screen.Library.route) {
                                popUpTo(Screen.Home.route) { inclusive = false }
                            }
                        }
                    )
                }
                composable(Screen.Library.route) {
                    LibraryScreen(
                        onCassetteClick = { id ->
                            playbackViewModel.play(id)
                            navController.navigate(Screen.Player.route(id)) {
                                launchSingleTop = true
                            }
                        }
                    )
                }
                composable(
                    route = Screen.Player.route,
                    arguments = listOf(navArgument("cassetteId") { type = NavType.IntType })
                ) {
                    PlayerScreen(
                        onBack = { navController.popBackStack() },
                        viewModel = playbackViewModel
                    )
                }
            }
        }
    }
}
