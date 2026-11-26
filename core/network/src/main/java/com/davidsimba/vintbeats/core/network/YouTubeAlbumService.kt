package com.davidsimba.vintbeats.core.network

import com.davidsimba.vintbeats.core.model.AlbumDetail
import javax.inject.Inject

class YouTubeAlbumService @Inject constructor(
    private val backendService: BackendService
) {
    suspend fun getAlbumDetail(browseId: String): AlbumDetail? = backendService.getAlbumDetail(browseId)
}
