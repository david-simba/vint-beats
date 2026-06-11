package com.davidsimba.vintbeats

import android.app.Application
import com.davidsimba.vintbeats.core.youtube.NewPipeDownloader
import com.davidsimba.vintbeats.feature.library.domain.track.TrackRepository
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.schabi.newpipe.extractor.NewPipe
import javax.inject.Inject

@HiltAndroidApp
class VintBeatsApp : Application() {

    @Inject lateinit var newPipeDownloader: NewPipeDownloader
    @Inject lateinit var trackRepository: TrackRepository

    override fun onCreate() {
        super.onCreate()
        NewPipe.init(newPipeDownloader)
        CoroutineScope(Dispatchers.IO).launch {
            trackRepository.resetStuckDownloads()
        }
    }
}
