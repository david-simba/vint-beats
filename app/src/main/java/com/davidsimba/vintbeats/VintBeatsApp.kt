package com.davidsimba.vintbeats

import android.app.Application
import com.davidsimba.vintbeats.core.data.stream.NewPipeInitializer
import com.davidsimba.vintbeats.core.data.library.TrackRepository
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltAndroidApp
class VintBeatsApp : Application() {

    @Inject lateinit var newPipeInitializer: NewPipeInitializer
    @Inject lateinit var trackRepository: TrackRepository

    override fun onCreate() {
        super.onCreate()
        newPipeInitializer.init()
        CoroutineScope(Dispatchers.IO).launch {
            trackRepository.resetStuckDownloads()
        }
    }
}
