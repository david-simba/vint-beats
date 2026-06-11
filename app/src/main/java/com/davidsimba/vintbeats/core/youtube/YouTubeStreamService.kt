package com.davidsimba.vintbeats.core.youtube

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.schabi.newpipe.extractor.ServiceList
import org.schabi.newpipe.extractor.stream.StreamInfo
import java.io.File
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Named

class YouTubeStreamService @Inject constructor(
    @Named("youtube") private val client: OkHttpClient,
    private val backendService: BackendService
) {
    companion object {
        private const val TAG = "YTStreamService"
    }

    suspend fun getAudioStreamUrl(videoId: String): String = withContext(Dispatchers.IO) {
        try {
            val info = StreamInfo.getInfo(
                ServiceList.YouTube,
                "https://www.youtube.com/watch?v=$videoId"
            )
            val audio = info.audioStreams
                .filter { it.content.isNotEmpty() }
                .maxByOrNull { it.bitrate }
            if (audio != null) {
                Log.d(TAG, "[$videoId] NewPipe → bitrate=${audio.bitrate}")
                return@withContext audio.content
            }
            Log.w(TAG, "[$videoId] NewPipe: no streams found, falling back to backend")
        } catch (e: Exception) {
            Log.w(TAG, "[$videoId] NewPipe failed (${e::class.simpleName}: ${e.message}), falling back to backend")
        }
        val url = backendService.streamProxyUrl(videoId)
        Log.d(TAG, "[$videoId] backend → $url")
        url
    }

    suspend fun downloadAudio(
        videoId: String,
        streamUrl: String,
        destDir: File,
        onProgress: ((Float) -> Unit)? = null
    ): String? = withContext(Dispatchers.IO) {
        val dir = File(destDir, "audio").also { it.mkdirs() }
        val file = File(dir, "$videoId.m4a")
        val tempFile = File(dir, "$videoId.tmp")
        try {
            if (file.exists() && file.length() > 0) {
                Log.d(TAG, "[$videoId] Already cached at ${file.path}")
                return@withContext file.path
            }
            if (tempFile.exists()) tempFile.delete()
            val downloadClient = OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(120, TimeUnit.SECONDS)
                .build()
            val request = Request.Builder().url(streamUrl).build()
            downloadClient.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    Log.e(TAG, "[$videoId] Download HTTP ${response.code}")
                    return@withContext null
                }
                val body = response.body ?: run {
                    Log.e(TAG, "[$videoId] Download: empty body")
                    return@withContext null
                }
                val contentLength = body.contentLength()
                var bytesCopied = 0L
                var lastReported = -0.02f
                val buffer = ByteArray(8 * 1024)
                tempFile.outputStream().buffered().use { out ->
                    val input = body.byteStream()
                    var bytes = input.read(buffer)
                    while (bytes >= 0) {
                        out.write(buffer, 0, bytes)
                        bytesCopied += bytes
                        if (onProgress != null && contentLength > 0) {
                            val progress = bytesCopied.toFloat() / contentLength.toFloat()
                            if (progress - lastReported >= 0.01f) {
                                onProgress(progress)
                                lastReported = progress
                            }
                        }
                        bytes = input.read(buffer)
                    }
                }
                if (tempFile.length() == 0L) {
                    Log.e(TAG, "[$videoId] Download: file empty after write")
                    tempFile.delete()
                    return@withContext null
                }
                tempFile.renameTo(file)
                Log.d(TAG, "[$videoId] Downloaded ${file.length() / 1024}KB → ${file.path}")
                file.path
            }
        } catch (e: Exception) {
            Log.e(TAG, "[$videoId] Download error: ${e::class.simpleName}: ${e.message}")
            null
        }
    }
}
