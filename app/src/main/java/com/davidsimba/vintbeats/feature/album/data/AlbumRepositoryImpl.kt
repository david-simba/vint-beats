package com.davidsimba.vintbeats.feature.album.data

import com.davidsimba.vintbeats.core.network.YouTubeAlbumService
import javax.inject.Inject
import com.davidsimba.vintbeats.core.model.AlbumDetail

class AlbumRepositoryImpl @Inject constructor(
    private val albumService: YouTubeAlbumService
) : AlbumRepository {
    override suspend fun getAlbumDetail(browseId: String): AlbumDetail =
        albumService.getAlbumDetail(browseId) ?: throw Exception("Could not load album")
}
