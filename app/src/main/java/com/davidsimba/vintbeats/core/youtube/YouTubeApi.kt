package com.davidsimba.vintbeats.core.youtube

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import okhttp3.Headers

internal const val YT_MUSIC_BASE_URL = "https://music.youtube.com/youtubei/v1"

internal const val YT_CLIENT_CONTEXT = """
    "context": {
      "client": {
        "clientName": "WEB_REMIX",
        "clientVersion": "1.20240501.01.00",
        "hl": "en",
        "gl": "US"
      }
    }
"""

internal fun buildWebHeaders(referer: String): Headers = Headers.Builder()
    .add("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/124.0.0.0 Safari/537.36")
    .add("Accept", "*/*")
    .add("Accept-Language", "en-US,en;q=0.5")
    .add("Content-Type", "application/json")
    .add("Origin", "https://music.youtube.com")
    .add("Referer", referer)
    .add("X-YouTube-Client-Name", "67")
    .add("X-YouTube-Client-Version", "1.20240501.01.00")
    .build()

internal fun buildAndroidHeaders(): Headers = Headers.Builder()
    .add("User-Agent", "com.google.android.youtube/17.31.35 (Linux; U; Android 11) gzip")
    .add("Content-Type", "application/json")
    .add("Accept-Language", "en-US,en;q=0.5")
    .add("X-YouTube-Client-Name", "3")
    .add("X-YouTube-Client-Version", "17.31.35")
    .build()

// JSON navigation helpers
internal fun JsonObject.obj(key: String): JsonObject? =
    get(key)?.takeIf { it.isJsonObject }?.asJsonObject

internal fun JsonObject.arr(key: String): JsonArray? =
    get(key)?.takeIf { it.isJsonArray }?.asJsonArray

internal fun JsonObject.str(key: String): String? =
    get(key)?.takeIf { it.isJsonPrimitive }?.asString

internal fun JsonArray.idx(i: Int) = if (i < size()) get(i) else null
internal fun JsonArray.last() = if (size() > 0) get(size() - 1) else null

internal fun String.escapeJson() = replace("\\", "\\\\").replace("\"", "\\\"")
