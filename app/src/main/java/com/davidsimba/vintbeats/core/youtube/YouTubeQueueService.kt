package com.davidsimba.vintbeats.core.youtube

import com.davidsimba.vintbeats.core.model.Track
import javax.inject.Inject

class YouTubeQueueService @Inject constructor(
    private val backendService: BackendService
) {
    suspend fun getUpNextTracks(videoId: String): List<Track> = backendService.getQueue(videoId)
}
