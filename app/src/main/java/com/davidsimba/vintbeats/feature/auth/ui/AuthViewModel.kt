package com.davidsimba.vintbeats.feature.auth.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.davidsimba.vintbeats.feature.auth.data.SpotifyAuthManager
import com.davidsimba.vintbeats.feature.auth.data.SpotifyAuthResult
import com.davidsimba.vintbeats.feature.auth.domain.AuthRepository
import com.spotify.sdk.android.auth.AuthorizationRequest
import com.spotify.sdk.android.auth.AuthorizationResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val spotifyAuthManager: SpotifyAuthManager
) : ViewModel() {

    val isLoggedIn: StateFlow<Boolean> = authRepository.isLoggedIn
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(500),
            initialValue = false
        )

    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    fun buildSpotifyAuthRequest(): AuthorizationRequest =
        spotifyAuthManager.buildAuthRequest()

    fun handleSpotifyResponse(response: AuthorizationResponse) {
        when (val result = spotifyAuthManager.parseResponse(response)) {
            is SpotifyAuthResult.Code -> viewModelScope.launch {
                _uiState.value = AuthUiState.Loading
                runCatching { spotifyAuthManager.exchangeCode(result.code) }
                    .onSuccess { token ->
                        authRepository.saveTokens(token.accessToken, token.refreshToken)
                        _uiState.value = AuthUiState.Idle
                    }
                    .onFailure { e ->
                        _uiState.value = AuthUiState.Error(e.message ?: "Error al obtener tokens")
                    }
            }
            is SpotifyAuthResult.Error -> _uiState.value = AuthUiState.Error(result.message)
            SpotifyAuthResult.Cancelled -> Unit
        }
    }

    fun clearError() {
        _uiState.value = AuthUiState.Idle
    }
}
