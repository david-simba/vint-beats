package com.davidsimba.vintbeats

import android.app.Application
import com.davidsimba.vintbeats.core.youtube.NewPipeDownloader
import dagger.hilt.android.HiltAndroidApp
import org.schabi.newpipe.extractor.NewPipe
import javax.inject.Inject

@HiltAndroidApp
class VintBeatsApp : Application() {

    @Inject
    lateinit var newPipeDownloader: NewPipeDownloader

    override fun onCreate() {
        super.onCreate()
        NewPipe.init(newPipeDownloader)
    }
}
