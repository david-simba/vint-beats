package com.davidsimba.vintbeats.feature.album.data

interface AlbumRepository {
    suspend fun getAlbumDetail(browseId: String): AlbumDetail
}
