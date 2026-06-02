package com.davidsimba.vintbeats.feature.library.domain

import kotlinx.coroutines.flow.Flow

interface PlaylistRepository {
    fun getPlaylists(): Flow<List<Playlist>>
    fun getPlaylistWithTracks(playlistId: Int): Flow<PlaylistWithTracks?>
    suspend fun createPlaylist(name: String, coverImagePath: String? = null): Int
    suspend fun deletePlaylist(playlistId: Int)
    suspend fun addTrackToPlaylist(playlistId: Int, savedTrackId: Int)
    suspend fun removeTrackFromPlaylist(playlistId: Int, savedTrackId: Int)
}
