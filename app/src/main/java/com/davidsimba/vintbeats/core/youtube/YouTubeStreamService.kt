package com.davidsimba.vintbeats.core.youtube

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
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
        val url = backendService.streamProxyUrl(videoId)
        Log.d(TAG, "[$videoId] proxy → $url")
        url
    }

    suspend fun downloadAudio(videoId: String, streamUrl: String, destDir: File): String? =
        withContext(Dispatchers.IO) {
            try {
                val dir = File(destDir, "audio").also { it.mkdirs() }
                val file = File(dir, "$videoId.m4a")
                if (file.exists() && file.length() > 0) {
                    Log.d(TAG, "[$videoId] Already cached at ${file.path}")
                    return@withContext file.path
                }
                val downloadClient = client.newBuilder()
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
                    file.outputStream().buffered().use { out ->
                        body.byteStream().copyTo(out)
                    }
                    if (file.length() == 0L) {
                        Log.e(TAG, "[$videoId] Download: file empty after write")
                        file.delete()
                        return@withContext null
                    }
                    Log.d(TAG, "[$videoId] Downloaded ${file.length() / 1024}KB → ${file.path}")
                    file.path
                }
            } catch (e: Exception) {
                Log.e(TAG, "[$videoId] Download error: ${e::class.simpleName}: ${e.message}")
                null
            }
        }
}
