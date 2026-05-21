package com.davidsimba.vintbeats.core.youtube

import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.RequestBody.Companion.toRequestBody
import org.schabi.newpipe.extractor.downloader.Downloader
import org.schabi.newpipe.extractor.downloader.Request
import org.schabi.newpipe.extractor.downloader.Response
import javax.inject.Inject
import javax.inject.Named

class NewPipeDownloader @Inject constructor(
    @Named("youtube") private val client: OkHttpClient
) : Downloader() {

    override fun execute(request: Request): Response {
        val builder = okhttp3.Request.Builder().url(request.url())

        request.headers().forEach { (key, values) ->
            values.forEach { value -> builder.addHeader(key, value) }
        }

        when (request.httpMethod()) {
            "POST" -> {
                val contentType = request.headers()["Content-Type"]
                    ?.firstOrNull() ?: "application/x-www-form-urlencoded"
                val data = request.dataToSend() ?: ByteArray(0)
                builder.post(data.toRequestBody(contentType.toMediaTypeOrNull()))
            }
            "HEAD" -> builder.head()
            else -> builder.get()
        }

        val okResponse = client.newCall(builder.build()).execute()
        return Response(
            okResponse.code,
            okResponse.message,
            okResponse.headers.toMultimap(),
            okResponse.body?.string(),
            okResponse.request.url.toString()
        )
    }
}
