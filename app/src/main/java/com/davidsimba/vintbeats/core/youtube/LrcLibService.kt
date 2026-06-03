package com.davidsimba.vintbeats.core.youtube

import android.util.Log
import com.davidsimba.vintbeats.feature.player.ui.LyricLine
import com.google.gson.JsonParser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.net.URLEncoder
import javax.inject.Inject
import javax.inject.Named

class LrcLibService @Inject constructor(
    @Named("youtube") private val client: OkHttpClient
) {
    companion object {
        private const val TAG = "LrcLibService"
        private val LRC_REGEX = Regex("""\[(\d+):(\d{2})\.(\d{2,3})](.*)""")
    }

    suspend fun getSyncedLyrics(title: String, artist: String): List<LyricLine> =
        withContext(Dispatchers.IO) {
            try {
                val query = URLEncoder.encode("$title $artist".trim(), "UTF-8")
                val request = Request.Builder()
                    .url("https://lrclib.net/api/search?q=$query")
                    .header("User-Agent", "VintBeats/1.0")
                    .build()

                client.newCall(request).execute().use { response ->
                    if (!response.isSuccessful) {
                        Log.w(TAG, "[$title] HTTP ${response.code}")
                        return@withContext emptyList()
                    }
                    val body = response.body?.string() ?: return@withContext emptyList()
                    val array = JsonParser.parseString(body).asJsonArray
                    for (el in array) {
                        val obj = el.asJsonObject
                        val synced = obj.get("syncedLyrics")
                            ?.takeIf { !it.isJsonNull }
                            ?.asString
                        if (!synced.isNullOrBlank()) {
                            val lines = parseLrc(synced)
                            if (lines.isNotEmpty()) {
                                Log.d(TAG, "[$title] ${lines.size} synced lines")
                                return@withContext lines
                            }
                        }
                    }
                    Log.d(TAG, "[$title] no synced lyrics found")
                    emptyList()
                }
            } catch (e: Exception) {
                Log.e(TAG, "[$title] error: ${e.message}")
                emptyList()
            }
        }

    private fun parseLrc(lrc: String): List<LyricLine> =
        lrc.lines().mapNotNull { line ->
            val match = LRC_REGEX.matchEntire(line.trim()) ?: return@mapNotNull null
            val (min, sec, cs, text) = match.destructured
            if (text.isBlank()) return@mapNotNull null
            val timeMs = min.toLong() * 60_000L + sec.toLong() * 1_000L +
                if (cs.length == 3) cs.toLong() else cs.toLong() * 10L
            LyricLine(timeMs, text.trim())
        }.sortedBy { it.timeMs }
}
