package com.davidsimba.vintbeats.core.youtube

import com.davidsimba.vintbeats.feature.album.data.AlbumDetail
import javax.inject.Inject

class YouTubeAlbumService @Inject constructor(
    private val backendService: BackendService
) {
    suspend fun getAlbumDetail(browseId: String): AlbumDetail? = backendService.getAlbumDetail(browseId)
}
