package com.davidsimba.vintbeats.navigation

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Download
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.davidsimba.vintbeats.R
import com.davidsimba.vintbeats.shared.SnackbarController
import com.davidsimba.vintbeats.shared.SnackbarEvent
import com.davidsimba.vintbeats.shared.components.SnackbarPosition
import com.davidsimba.vintbeats.shared.components.VintSnackbar
import kotlinx.coroutines.delay
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.navigation
import androidx.navigation.navArgument
import com.davidsimba.vintbeats.feature.album.ui.AlbumScreen
import com.davidsimba.vintbeats.feature.artist.ui.ArtistScreen
import com.davidsimba.vintbeats.feature.library.ui.addsongs.AddSongsScreen
import com.davidsimba.vintbeats.feature.library.ui.addtoplaylist.AddToPlaylistScreen
import com.davidsimba.vintbeats.feature.onboarding.ui.OnboardingScreen
import com.davidsimba.vintbeats.feature.library.ui.editplaylist.EditPlaylistScreen
import com.davidsimba.vintbeats.feature.library.ui.createplaylist.CreatePlaylistScreen
import com.davidsimba.vintbeats.feature.library.ui.downloads.DownloadsScreen
import com.davidsimba.vintbeats.feature.library.ui.favorites.FavoritesScreen
import com.davidsimba.vintbeats.feature.library.ui.library.LibraryScreen
import com.davidsimba.vintbeats.feature.library.ui.userplaylist.UserPlaylistScreen
import com.davidsimba.vintbeats.feature.home.ui.HomeScreen
import com.davidsimba.vintbeats.feature.player.ui.PlaybackViewModel
import com.davidsimba.vintbeats.feature.player.ui.PlayerScreen
import com.davidsimba.vintbeats.feature.player.ui.PlayerState
import com.davidsimba.vintbeats.feature.playlist.PlaylistScreen
import com.davidsimba.vintbeats.feature.search.ui.SearchActiveScreen
import com.davidsimba.vintbeats.feature.search.ui.SearchScreen
import com.davidsimba.vintbeats.feature.search.ui.SearchViewModel
import com.davidsimba.vintbeats.feature.player.ui.components.MiniPlayer
import com.davidsimba.vintbeats.shared.components.background.Background
import com.davidsimba.vintbeats.shared.components.navbar.BottomNavBar

private val bottomNavRoutes = setOf(
    Screen.Home.route,
    Screen.Search.route,
    Screen.SearchActive.route,
    Screen.Library.route,
    Screen.Artist.route,
    Screen.Album.route,
    Screen.Playlist.route,
    Screen.Favorites.route,
    Screen.Downloads.route,
    Screen.CreatePlaylist.route,
    Screen.UserPlaylist.route,
    Screen.AddSongs.route,
    Screen.EditPlaylist.route,
    Screen.AddToPlaylist.route,
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

    var snackbarEvent by remember { mutableStateOf<SnackbarEvent?>(null) }
    val downloadStartedMsg = stringResource(R.string.download_started)
    val downloadSuccessMsg = stringResource(R.string.download_success)

    LaunchedEffect(Unit) {
        SnackbarController.events.collect { event ->
            snackbarEvent = event
            delay(3000)
            snackbarEvent = null
        }
    }

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
    val isFavorite by playbackViewModel.isFavorite.collectAsStateWithLifecycle()
    val playingTrackId by playbackViewModel.currentlyPlayingTrackId.collectAsStateWithLifecycle()
    val isTrackPlaying = playbackState is PlayerState.Playing

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
                        isFavorite = isFavorite,
                        onToggleFavorite = playbackViewModel::toggleFavorite,
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
                enterTransition = { fadeIn(animationSpec = tween(180)) },
                exitTransition = { fadeOut(animationSpec = tween(180)) },
                popEnterTransition = { EnterTransition.None },
                popExitTransition = { ExitTransition.None }
            ) {
                composable(
                    route = Screen.Home.route,
                    popEnterTransition = { fadeIn(animationSpec = tween(180)) }
                ) {
                    HomeScreen(
                        playingTrackId = playingTrackId,
                        isTrackPlaying = isTrackPlaying,
                        onTrackSelected = { track, queue ->
                            playbackViewModel.playTrack(track, newQueue = queue)
                        },
                        onPlaylistSelected = { id, thumbnailUrl, artistId, artistName ->
                            navController.navigate(Screen.Playlist.route(id, thumbnailUrl, artistId, artistName))
                        },
                        onAlbumSelected = { id ->
                            navController.navigate(Screen.Album.route(id))
                        },
                        onRadioSelected = { radio ->
                            if (radio.tracks.isNotEmpty()) {
                                playbackViewModel.playTrack(
                                    radio.tracks.first(),
                                    newQueue = radio.tracks.drop(1)
                                )
                            }
                        },
                        onNavigateToOnboarding = {
                            navController.navigate(Screen.Onboarding.route) {
                                launchSingleTop = true
                            }
                        },
                        onNavigateToAddToPlaylist = {
                            navController.navigate(Screen.AddToPlaylist.route)
                        }
                    )
                }
                composable(
                    route = Screen.Onboarding.route,
                    enterTransition = { slideInVertically(animationSpec = tween(300), initialOffsetY = { it }) },
                    exitTransition = { ExitTransition.None },
                    popEnterTransition = { EnterTransition.None },
                    popExitTransition = { slideOutVertically(animationSpec = tween(300), targetOffsetY = { it }) }
                ) {
                    OnboardingScreen(
                        onDone = { navController.popBackStack() }
                    )
                }
                navigation(
                    startDestination = Screen.Search.route,
                    route = "search_graph"
                ) {
                    composable(
                        route = Screen.Search.route,
                        popEnterTransition = { fadeIn(animationSpec = tween(120)) },
                        popExitTransition = { fadeOut(animationSpec = tween(180)) }
                    ) { entry ->
                        val parentEntry = remember(entry) {
                            navController.getBackStackEntry("search_graph")
                        }
                        val viewModel: SearchViewModel = hiltViewModel(parentEntry)
                        SearchScreen(
                            onSearchTap = {
                                navController.navigate(Screen.SearchActive.route) {
                                    launchSingleTop = true
                                }
                            },
                            onPlaylistSelected = { playlist ->
                                navController.navigate(Screen.Playlist.route(playlist.id))
                            },
                            viewModel = viewModel
                        )
                    }
                    composable(
                        route = Screen.SearchActive.route,
                        enterTransition = { fadeIn(animationSpec = tween(120)) },
                        exitTransition = { ExitTransition.None },
                        popEnterTransition = { EnterTransition.None },
                        popExitTransition = { ExitTransition.None }
                    ) { entry ->
                        val parentEntry = remember(entry) {
                            navController.getBackStackEntry("search_graph")
                        }
                        val viewModel: SearchViewModel = hiltViewModel(parentEntry)
                        SearchActiveScreen(
                            onBack = {
                                viewModel.onQueryChange("")
                                navController.popBackStack()
                            },
                            onTrackSelected = { track ->
                                playbackViewModel.playTrack(track)
                            },
                            onArtistSelected = { artist ->
                                navController.navigate(Screen.Artist.route(artist.id))
                            },
                            onAlbumSelected = { album ->
                                navController.navigate(Screen.Album.route(album.id))
                            },
                            viewModel = viewModel
                        )
                    }
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
                        },
                        onPlayArtist = { tracks ->
                            if (tracks.isNotEmpty()) {
                                playbackViewModel.playTrack(tracks.first(), newQueue = tracks.drop(1))
                            }
                        },
                        onAlbumSelected = { album ->
                            navController.navigate(Screen.Album.route(album.id))
                        },
                        onNavigateToAddToPlaylist = {
                            navController.navigate(Screen.AddToPlaylist.route)
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
                        onTrackSelected = { track, queue ->
                            playbackViewModel.playTrack(track, newQueue = queue)
                        },
                        onPlayAlbum = { tracks ->
                            if (tracks.isNotEmpty()) {
                                playbackViewModel.playTrack(tracks.first(), newQueue = tracks.drop(1))
                            }
                        },
                        onNavigateToAddToPlaylist = {
                            navController.navigate(Screen.AddToPlaylist.route)
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
                        onArtistSelected = { browseId ->
                            navController.navigate(Screen.Artist.route(browseId))
                        },
                        onNavigateToAddToPlaylist = {
                            navController.navigate(Screen.AddToPlaylist.route)
                        },
                        viewModel = playbackViewModel
                    )
                }
                composable(
                    route = Screen.Playlist.route,
                    arguments = listOf(
                        navArgument("playlistId") { type = NavType.StringType },
                        navArgument("thumbnailUrl") { type = NavType.StringType; nullable = true; defaultValue = null },
                        navArgument("artistId") { type = NavType.StringType; nullable = true; defaultValue = null },
                        navArgument("artistName") { type = NavType.StringType; nullable = true; defaultValue = null },
                    ),
                    enterTransition = { slideInHorizontally(animationSpec = tween(220), initialOffsetX = { it }) },
                    exitTransition = { ExitTransition.None },
                    popEnterTransition = { EnterTransition.None },
                    popExitTransition = { slideOutHorizontally(animationSpec = tween(220), targetOffsetX = { it }) }
                ) {
                    PlaylistScreen(
                        playingTrackId = playingTrackId,
                        isTrackPlaying = isTrackPlaying,
                        onBack = { navController.popBackStack() },
                        onTrackSelected = { track, queue ->
                            playbackViewModel.playTrack(track, newQueue = queue.filter { it.id != track.id })
                        },
                        onPlayAll = { tracks ->
                            if (tracks.isNotEmpty()) {
                                playbackViewModel.playTrack(tracks.first(), newQueue = tracks.drop(1))
                            }
                        },
                        onNavigateToAddToPlaylist = {
                            navController.navigate(Screen.AddToPlaylist.route)
                        }
                    )
                }
                composable(
                    route = Screen.Library.route,
                    popExitTransition = { fadeOut(animationSpec = tween(180)) }
                ) {
                    LibraryScreen(
                        onFavoritesClick = { navController.navigate(Screen.Favorites.route) },
                        onDownloadsClick = { navController.navigate(Screen.Downloads.route) },
                        onPlaylistClick = { id -> navController.navigate(Screen.UserPlaylist.route(id)) },
                        onCreatePlaylistClick = { navController.navigate(Screen.CreatePlaylist.route) },
                        onAlbumClick = { id -> navController.navigate(Screen.Album.route(id)) },
                        onArtistClick = { id -> navController.navigate(Screen.Artist.route(id)) },
                    )
                }
                composable(
                    route = Screen.CreatePlaylist.route,
                    arguments = listOf(navArgument("playlistId") {
                        type = NavType.IntType
                        defaultValue = -1
                    }),
                    enterTransition = { slideInHorizontally(animationSpec = tween(220), initialOffsetX = { it }) },
                    exitTransition = { ExitTransition.None },
                    popEnterTransition = { EnterTransition.None },
                    popExitTransition = { slideOutHorizontally(animationSpec = tween(220), targetOffsetX = { it }) },
                ) { backStackEntry ->
                    val editId = backStackEntry.arguments?.getInt("playlistId")?.takeIf { it != -1 }
                    CreatePlaylistScreen(
                        onBack = { navController.popBackStack() },
                        onCreated = { playlistId ->
                            if (editId != null) {
                                navController.popBackStack()
                            } else {
                                navController.navigate(Screen.UserPlaylist.route(playlistId)) {
                                    popUpTo(Screen.CreatePlaylist.route()) { inclusive = true }
                                }
                            }
                        },
                    )
                }
                composable(
                    route = Screen.UserPlaylist.route,
                    arguments = listOf(navArgument("playlistId") { type = NavType.IntType }),
                    enterTransition = { slideInHorizontally(animationSpec = tween(220), initialOffsetX = { it }) },
                    exitTransition = { ExitTransition.None },
                    popEnterTransition = { EnterTransition.None },
                    popExitTransition = { slideOutHorizontally(animationSpec = tween(220), targetOffsetX = { it }) },
                ) {
                    val playlistId = it.arguments?.getInt("playlistId") ?: return@composable
                    UserPlaylistScreen(
                        playingTrackId = playingTrackId,
                        isTrackPlaying = isTrackPlaying,
                        onBack = { navController.popBackStack() },
                        onTrackClick = { id -> playbackViewModel.play(id) },
                        onPlayAll = { tracks ->
                            if (tracks.isNotEmpty()) {
                                playbackViewModel.play(tracks.first().id)
                            }
                        },
                        onAddSongsClick = { navController.navigate(Screen.AddSongs.route(playlistId)) },
                        onEditClick = { navController.navigate(Screen.EditPlaylist.route(playlistId)) },
                        onEditInfoClick = { navController.navigate(Screen.CreatePlaylist.route(playlistId)) },
                        onNavigateToAddToPlaylist = {
                            navController.navigate(Screen.AddToPlaylist.route)
                        }
                    )
                }
                composable(
                    route = Screen.EditPlaylist.route,
                    arguments = listOf(navArgument("playlistId") { type = NavType.IntType }),
                    enterTransition = { slideInHorizontally(animationSpec = tween(220), initialOffsetX = { it }) },
                    exitTransition = { ExitTransition.None },
                    popEnterTransition = { EnterTransition.None },
                    popExitTransition = { slideOutHorizontally(animationSpec = tween(220), targetOffsetX = { it }) }
                ) {
                    EditPlaylistScreen(onBack = { navController.popBackStack() })
                }
                composable(
                    route = Screen.AddSongs.route,
                    arguments = listOf(navArgument("playlistId") { type = NavType.IntType }),
                    enterTransition = { slideInHorizontally(animationSpec = tween(220), initialOffsetX = { it }) },
                    exitTransition = { ExitTransition.None },
                    popEnterTransition = { EnterTransition.None },
                    popExitTransition = { slideOutHorizontally(animationSpec = tween(220), targetOffsetX = { it }) }
                ) {
                    AddSongsScreen(onBack = { navController.popBackStack() })
                }
                composable(
                    route = Screen.AddToPlaylist.route,
                    enterTransition = { slideInHorizontally(animationSpec = tween(220), initialOffsetX = { it }) },
                    exitTransition = { ExitTransition.None },
                    popEnterTransition = { EnterTransition.None },
                    popExitTransition = { slideOutHorizontally(animationSpec = tween(220), targetOffsetX = { it }) }
                ) {
                    AddToPlaylistScreen(onBack = { navController.popBackStack() })
                }
                composable(
                    route = Screen.Downloads.route,
                    enterTransition = { slideInHorizontally(animationSpec = tween(220), initialOffsetX = { it }) },
                    exitTransition = { ExitTransition.None },
                    popEnterTransition = { EnterTransition.None },
                    popExitTransition = { slideOutHorizontally(animationSpec = tween(220), targetOffsetX = { it }) }
                ) {
                    DownloadsScreen(
                        playingTrackId = playingTrackId,
                        isTrackPlaying = isTrackPlaying,
                        onBack = { navController.popBackStack() },
                        onTrackClick = { id -> playbackViewModel.play(id) },
                        onNavigateToAddToPlaylist = {
                            navController.navigate(Screen.AddToPlaylist.route)
                        }
                    )
                }
                composable(
                    route = Screen.Favorites.route,
                    enterTransition = { slideInHorizontally(animationSpec = tween(220), initialOffsetX = { it }) },
                    exitTransition = { ExitTransition.None },
                    popEnterTransition = { EnterTransition.None },
                    popExitTransition = { slideOutHorizontally(animationSpec = tween(220), targetOffsetX = { it }) }
                ) {
                    FavoritesScreen(
                        playingTrackId = playingTrackId,
                        isTrackPlaying = isTrackPlaying,
                        onBack = { navController.popBackStack() },
                        onTrackClick = { id -> playbackViewModel.play(id) },
                        onNavigateToAddToPlaylist = {
                            navController.navigate(Screen.AddToPlaylist.route)
                        }
                    )
                }
            }

            VintSnackbar(
                message = when (snackbarEvent) {
                    is SnackbarEvent.DownloadStarted -> downloadStartedMsg
                    is SnackbarEvent.DownloadSuccess -> downloadSuccessMsg
                    null -> ""
                },
                icon = when (snackbarEvent) {
                    is SnackbarEvent.DownloadSuccess -> Icons.Rounded.CheckCircle
                    else -> Icons.Rounded.Download
                },
                visible = snackbarEvent != null,
                position = SnackbarPosition.TOP,
                modifier = Modifier.align(Alignment.TopCenter)
            )
        }
    }
}
