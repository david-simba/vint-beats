package com.davidsimba.vintbeats.feature.search.data

import retrofit2.http.GET
import retrofit2.http.Query

interface SpotifySearchApi {
    @GET("search")
    suspend fun searchTracks(
        @Query("q") query: String,
        @Query("type") type: String,
        @Query("limit") limit: Int
    ) : SpotifySearchResponse
}