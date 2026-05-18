package com.davidsimba.vintbeats.feature.auth.data

import com.davidsimba.vintbeats.BuildConfig
import com.spotify.sdk.android.auth.AuthorizationRequest
import com.spotify.sdk.android.auth.AuthorizationResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.security.MessageDigest
import java.security.SecureRandom
import android.util.Base64
import com.davidsimba.vintbeats.feature.auth.domain.AuthToken
import javax.inject.Inject
import javax.inject.Singleton

sealed interface SpotifyAuthResult {
    data class Code(val code: String) : SpotifyAuthResult
    data class Error(val message: String) : SpotifyAuthResult
    data object Cancelled : SpotifyAuthResult
}

@Singleton
class SpotifyAuthManager @Inject constructor() {

    private val tokenClient = OkHttpClient()
    private var codeVerifier: String = ""

    fun buildAuthRequest(): AuthorizationRequest {
        codeVerifier = generateCodeVerifier()
        val codeChallenge = generateCodeChallenge(codeVerifier)

        return AuthorizationRequest.Builder(
            BuildConfig.SPOTIFY_CLIENT_ID,
            AuthorizationResponse.Type.CODE,
            BuildConfig.SPOTIFY_REDIRECT_URI
        )
        .setScopes(arrayOf(
            "user-read-private",
            "user-read-email",
            "user-library-read",
            "user-top-read",
            "streaming",
            "playlist-read-private"
        ))
        .setCustomParam("code_challenge_method", "S256")
        .setCustomParam("code_challenge", codeChallenge)
        .build()
    }

    suspend fun exchangeCode(code: String): AuthToken = withContext(Dispatchers.IO) {
        val body = FormBody.Builder()
            .add("grant_type", "authorization_code")
            .add("code", code)
            .add("redirect_uri", BuildConfig.SPOTIFY_REDIRECT_URI)
            .add("client_id", BuildConfig.SPOTIFY_CLIENT_ID)
            .add("code_verifier", codeVerifier)
            .build()

        val request = Request.Builder()
            .url("https://accounts.spotify.com/api/token")
            .post(body)
            .build()

        val response = tokenClient.newCall(request).execute()
        val responseBody = response.body?.string()
            ?: error("Empty response from Spotify token endpoint")

        if (!response.isSuccessful) error("Token exchange failed: $responseBody")

        val json = JSONObject(responseBody)
        AuthToken(
            accessToken = json.getString("access_token"),
            refreshToken = json.getString("refresh_token")
        )
    }

    fun parseResponse(response: AuthorizationResponse): SpotifyAuthResult =
        when (response.type) {
            AuthorizationResponse.Type.CODE -> SpotifyAuthResult.Code(response.code)
            AuthorizationResponse.Type.ERROR -> SpotifyAuthResult.Error(response.error)
            else -> SpotifyAuthResult.Cancelled
        }

    private fun generateCodeVerifier(): String {
        val bytes = ByteArray(32)
        SecureRandom().nextBytes(bytes)
        return Base64.encodeToString(bytes, Base64.URL_SAFE or Base64.NO_PADDING or Base64.NO_WRAP)
    }

    private fun generateCodeChallenge(verifier: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
            .digest(verifier.toByteArray(Charsets.US_ASCII))
        return Base64.encodeToString(digest, Base64.URL_SAFE or Base64.NO_PADDING or Base64.NO_WRAP)
    }
}
