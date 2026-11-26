package com.davidsimba.vintbeats.core.data.stream

import org.schabi.newpipe.extractor.NewPipe
import javax.inject.Inject
import javax.inject.Singleton

/** Inicializa NewPipe una vez al arrancar la app. */
@Singleton
class NewPipeInitializer @Inject constructor(
    private val downloader: NewPipeDownloader,
) {
    fun init() = NewPipe.init(downloader)
}
