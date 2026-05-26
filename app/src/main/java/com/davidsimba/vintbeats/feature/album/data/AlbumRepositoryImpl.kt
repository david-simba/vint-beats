package com.davidsimba.vintbeats.feature.album.data

import com.davidsimba.vintbeats.core.youtube.YouTubeAlbumService
import javax.inject.Inject

class AlbumRepositoryImpl @Inject constructor(
    private val albumService: YouTubeAlbumService
) : AlbumRepository {
    override suspend fun getAlbumDetail(browseId: String): AlbumDetail =
        albumService.getAlbumDetail(browseId) ?: throw Exception("Could not load album")
}
