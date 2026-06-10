package com.davidsimba.vintbeats.shared

data class CollectionPlaybackState(
    val playingTrackId: String? = null,
    val isTrackPlaying: Boolean = false,
    val playingFromRoute: String? = null,
    val thisRoute: String? = null,
    val onSetPlayingFrom: ((String) -> Unit)? = null,
) {
    private fun isFromHere(): Boolean =
        thisRoute == null || playingFromRoute == null || playingFromRoute == thisRoute

    fun isPlayingFrom(trackIds: List<String>): Boolean =
        isTrackPlaying && isFromHere() && trackIds.any { it == playingTrackId }

    fun isActive(trackId: String): Boolean = trackId == playingTrackId && isFromHere()
    fun isPlaying(trackId: String): Boolean = trackId == playingTrackId && isTrackPlaying && isFromHere()

    fun notifyPlaying(name: String) = onSetPlayingFrom?.invoke(name)
}
