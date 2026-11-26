package com.davidsimba.vintbeats.feature.album.data

import com.davidsimba.vintbeats.core.model.AlbumDetail

interface AlbumRepository {
    suspend fun getAlbumDetail(browseId: String): AlbumDetail
}
