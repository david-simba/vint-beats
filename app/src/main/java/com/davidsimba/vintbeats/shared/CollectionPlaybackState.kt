package com.davidsimba.vintbeats.shared

data class CollectionPlaybackState(
    val playingTrackId: String? = null,
    val isTrackPlaying: Boolean = false,
    val onSetPlayingFrom: ((String) -> Unit)? = null,
) {
    fun isPlayingFrom(trackIds: List<String>): Boolean =
        isTrackPlaying && trackIds.any { it == playingTrackId }

    fun isActive(trackId: String): Boolean = trackId == playingTrackId
    fun isPlaying(trackId: String): Boolean = trackId == playingTrackId && isTrackPlaying

    fun notifyPlaying(name: String) = onSetPlayingFrom?.invoke(name)
}
