package com.davidsimba.vintbeats.core.youtube

import android.util.Log
import com.google.gson.JsonParser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Named

class YouTubeStreamService @Inject constructor(
    @Named("youtube") private val client: OkHttpClient
) {
    companion object {
        private const val TAG = "YTStreamService"
    }

    suspend fun getAudioStreamUrl(videoId: String): String? = withContext(Dispatchers.IO) {
        for (attempt in 1..2) {
            val url = tryNewPipe(videoId, attempt)
            if (url != null) return@withContext url
        }
        Log.d(TAG, "[$videoId] NewPipe exhausted, trying InnerTube ANDROID fallback")
        tryInnerTube(videoId)
    }

    private fun tryNewPipe(videoId: String, attempt: Int): String? {
        return try {
            val streamInfo = org.schabi.newpipe.extractor.stream.StreamInfo.getInfo(
                org.schabi.newpipe.extractor.NewPipe.getService(0),
                "https://www.youtube.com/watch?v=$videoId"
            )
            val best = streamInfo.audioStreams
                .filter { !it.content.isNullOrEmpty() }
                .maxByOrNull { it.averageBitrate }
            if (best != null) {
                Log.d(TAG, "[$videoId] NewPipe attempt $attempt → ${streamInfo.audioStreams.size} streams, ${best.averageBitrate}bps")
                best.content
            } else {
                Log.w(TAG, "[$videoId] NewPipe attempt $attempt → no audio streams")
                null
            }
        } catch (e: Exception) {
            Log.w(TAG, "[$videoId] NewPipe attempt $attempt → ${e::class.simpleName}: ${e.message}")
            null
        }
    }

    private fun tryInnerTube(videoId: String): String? {
        return try {
            val body = """
                {
                  "context": {
                    "client": {
                      "clientName": "ANDROID",
                      "clientVersion": "17.31.35",
                      "androidSdkVersion": 30,
                      "hl": "en",
                      "gl": "US"
                    }
                  },
                  "videoId": "$videoId"
                }
            """.trimIndent().toRequestBody("application/json".toMediaType())

            val request = Request.Builder()
                .url("https://www.youtube.com/youtubei/v1/player?prettyPrint=false")
                .post(body)
                .headers(buildAndroidHeaders())
                .build()

            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    Log.e(TAG, "[$videoId] InnerTube → HTTP ${response.code}")
                    return null
                }
                val json = JsonParser.parseString(response.body?.string() ?: return null).asJsonObject
                val status = json.obj("playabilityStatus")?.str("status")
                if (status != "OK") {
                    Log.e(TAG, "[$videoId] InnerTube → playability=$status")
                    return null
                }
                val formats = json.obj("streamingData")?.arr("adaptiveFormats") ?: run {
                    Log.e(TAG, "[$videoId] InnerTube → no adaptiveFormats")
                    return null
                }
                var bestBitrate = 0
                var bestUrl: String? = null
                for (el in formats) {
                    val fmt = el.asJsonObject
                    if (!fmt.has("audioQuality") || fmt.has("qualityLabel")) continue
                    val url = fmt.str("url") ?: continue
                    val bitrate = fmt.get("bitrate")?.asInt ?: 0
                    if (bitrate > bestBitrate) {
                        bestBitrate = bitrate
                        bestUrl = url
                    }
                }
                if (bestUrl != null) {
                    Log.d(TAG, "[$videoId] InnerTube → audio stream ${bestBitrate}bps")
                } else {
                    Log.e(TAG, "[$videoId] InnerTube → no plain audio streams found")
                }
                bestUrl
            }
        } catch (e: Exception) {
            Log.e(TAG, "[$videoId] InnerTube → ${e::class.simpleName}: ${e.message}")
            null
        }
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
