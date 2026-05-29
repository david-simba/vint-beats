package com.davidsimba.vintbeats.navigation

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navArgument
import com.davidsimba.vintbeats.feature.album.ui.AlbumScreen
import com.davidsimba.vintbeats.feature.artist.ui.ArtistScreen
import com.davidsimba.vintbeats.feature.home.ui.HomeScreen
import com.davidsimba.vintbeats.feature.library.ui.LibraryScreen
import com.davidsimba.vintbeats.feature.player.ui.PlaybackViewModel
import com.davidsimba.vintbeats.feature.player.ui.PlayerScreen
import com.davidsimba.vintbeats.feature.playlist.PlaylistScreen
import com.davidsimba.vintbeats.feature.search.ui.SearchScreen
import com.davidsimba.vintbeats.feature.player.ui.components.MiniPlayer
import com.davidsimba.vintbeats.shared.components.background.Background
import com.davidsimba.vintbeats.shared.components.navbar.BottomNavBar

private val bottomNavRoutes = setOf(
    Screen.Home.route,
    Screen.Search.route,
    Screen.Library.route,
    Screen.Artist.route,
    Screen.Album.route,
    Screen.Playlist.route
)

@Composable
fun NavGraph(
    navController: NavHostController,
    startDestination: String,
    pendingNavRoute: String? = null,
    onPendingNavHandled: () -> Unit = {},
) {
    LaunchedEffect(pendingNavRoute) {
        if (pendingNavRoute != null) {
            navController.navigate(pendingNavRoute) { launchSingleTop = true }
            onPendingNavHandled()
        }
    }
    val currentEntry by navController.currentBackStackEntryAsState()
    val currentRoute = currentEntry?.destination?.route
    val showBottomBar = currentRoute in bottomNavRoutes

    val playbackViewModel: PlaybackViewModel = hiltViewModel()
    val currentSavedTrack by playbackViewModel.currentSavedTrack.collectAsStateWithLifecycle()
    val isSaved by playbackViewModel.isSaved.collectAsStateWithLifecycle()
    val unsavedTrack by playbackViewModel.unsavedTrack.collectAsStateWithLifecycle()
    val playbackState by playbackViewModel.playerState.collectAsStateWithLifecycle()
    val positionMs by playbackViewModel.positionMs.collectAsStateWithLifecycle()
    val durationMs by playbackViewModel.durationMs.collectAsStateWithLifecycle()
    val queue by playbackViewModel.queue.collectAsStateWithLifecycle()
    val history by playbackViewModel.history.collectAsStateWithLifecycle()
    val nextTrack = queue.firstOrNull()
    val previousTrack = history.lastOrNull()

    val hasActivePlayback = (isSaved && currentSavedTrack != null) || (!isSaved && unsavedTrack != null)
    val showMiniPlayer = hasActivePlayback && currentRoute != Screen.Player.route

    val miniTitle: String?
    val miniArtist: String
    val miniThumbnail: String?
    when {
        isSaved && currentSavedTrack != null -> {
            miniTitle = currentSavedTrack!!.trackTitle
            miniArtist = currentSavedTrack!!.trackArtist
            miniThumbnail = currentSavedTrack!!.trackThumbnailUrl
        }
        !isSaved && unsavedTrack != null -> {
            miniTitle = unsavedTrack!!.title
            miniArtist = unsavedTrack!!.artist
            miniThumbnail = unsavedTrack!!.albumImageUrl
        }
        else -> {
            miniTitle = null
            miniArtist = ""
            miniThumbnail = null
        }
    }

    Scaffold(
        contentWindowInsets = WindowInsets(0),
        bottomBar = {
            Column(modifier = if (!showBottomBar) Modifier.navigationBarsPadding() else Modifier) {
                if (showMiniPlayer && miniTitle != null) {
                    MiniPlayer(
                        title = miniTitle,
                        artist = miniArtist,
                        thumbnailUrl = miniThumbnail,
                        playerState = playbackState,
                        positionMs = positionMs,
                        durationMs = durationMs,
                        nextTrack = nextTrack,
                        previousTrack = previousTrack,
                        onTogglePlayPause = playbackViewModel::togglePlayPause,
                        onSkipNext = playbackViewModel::skipToNext,
                        onSkipPrevious = playbackViewModel::skipToPrevious,
                        onTap = { navController.navigate(Screen.Player.route) { launchSingleTop = true } }
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
                modifier = Modifier.padding(innerPadding),
                enterTransition = { EnterTransition.None },
                exitTransition = { ExitTransition.None },
                popEnterTransition = { EnterTransition.None },
                popExitTransition = { ExitTransition.None }
            ) {
                composable(Screen.Home.route) {
                    HomeScreen()
                }
                composable(Screen.Search.route) {
                    SearchScreen(
                        onTrackSelected = { track ->
                            playbackViewModel.playTrack(track)
                            navController.navigate(Screen.Player.route) { launchSingleTop = true }
                        },
                        onArtistSelected = { artist ->
                            navController.navigate(Screen.Artist.route(artist.id))
                        },
                        onAlbumSelected = { album ->
                            navController.navigate(Screen.Album.route(album.id))
                        },
                        onPlaylistSelected = { playlist ->
                            navController.navigate(Screen.Playlist.route(playlist.id))
                        }
                    )
                }
                composable(
                    route = Screen.Artist.route,
                    arguments = listOf(navArgument("browseId") { type = NavType.StringType }),
                    enterTransition = { slideInHorizontally(animationSpec = tween(220), initialOffsetX = { it }) },
                    exitTransition = { ExitTransition.None },
                    popEnterTransition = { EnterTransition.None },
                    popExitTransition = { slideOutHorizontally(animationSpec = tween(220), targetOffsetX = { it }) }
                ) {
                    ArtistScreen(
                        onBack = { navController.popBackStack() },
                        onTrackSelected = { track ->
                            playbackViewModel.playTrack(track)
                            navController.navigate(Screen.Player.route) { launchSingleTop = true }
                        },
                        onPlayArtist = { tracks ->
                            if (tracks.isNotEmpty()) {
                                playbackViewModel.playTrack(tracks.first(), newQueue = tracks.drop(1))
                                navController.navigate(Screen.Player.route) { launchSingleTop = true }
                            }
                        },
                        onAlbumSelected = { album ->
                            navController.navigate(Screen.Album.route(album.id))
                        }
                    )
                }
                composable(
                    route = Screen.Album.route,
                    arguments = listOf(navArgument("browseId") { type = NavType.StringType }),
                    enterTransition = { slideInHorizontally(animationSpec = tween(220), initialOffsetX = { it }) },
                    exitTransition = { ExitTransition.None },
                    popEnterTransition = { EnterTransition.None },
                    popExitTransition = { slideOutHorizontally(animationSpec = tween(220), targetOffsetX = { it }) }
                ) {
                    AlbumScreen(
                        onBack = { navController.popBackStack() },
                        onTrackSelected = { track ->
                            playbackViewModel.playTrack(track)
                            navController.navigate(Screen.Player.route) { launchSingleTop = true }
                        },
                        onPlayAlbum = { tracks ->
                            if (tracks.isNotEmpty()) {
                                playbackViewModel.playTrack(tracks.first(), newQueue = tracks.drop(1))
                                navController.navigate(Screen.Player.route) { launchSingleTop = true }
                            }
                        }
                    )
                }
                composable(
                    route = Screen.Player.route,
                    enterTransition = { slideInVertically(animationSpec = tween(200), initialOffsetY = { it }) },
                    exitTransition = { ExitTransition.None },
                    popEnterTransition = { EnterTransition.None },
                    popExitTransition = { slideOutVertically(animationSpec = tween(200), targetOffsetY = { it }) }
                ) {
                    PlayerScreen(
                        onBack = { navController.popBackStack() },
                        viewModel = playbackViewModel
                    )
                }
                composable(
                    route = Screen.Playlist.route,
                    arguments = listOf(navArgument("playlistId") { type = NavType.StringType }),
                    enterTransition = { slideInHorizontally(animationSpec = tween(220), initialOffsetX = { it }) },
                    exitTransition = { ExitTransition.None },
                    popEnterTransition = { EnterTransition.None },
                    popExitTransition = { slideOutHorizontally(animationSpec = tween(220), targetOffsetX = { it }) }
                ) {
                    PlaylistScreen(
                        onBack = { navController.popBackStack() },
                        onTrackSelected = { track, queue ->
                            playbackViewModel.playTrack(track, newQueue = queue.filter { it.id != track.id })
                            navController.navigate(Screen.Player.route) { launchSingleTop = true }
                        },
                        onPlayAll = { tracks ->
                            if (tracks.isNotEmpty()) {
                                playbackViewModel.playTrack(tracks.first(), newQueue = tracks.drop(1))
                                navController.navigate(Screen.Player.route) { launchSingleTop = true }
                            }
                        }
                    )
                }
                composable(Screen.Library.route) {
                    LibraryScreen(
                        onTrackClick = { id ->
                            playbackViewModel.play(id)
                            navController.navigate(Screen.Player.route) { launchSingleTop = true }
                        }
                    )
                }
            }
        }
    }
}
