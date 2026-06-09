package com.davidsimba.vintbeats.feature.profile.ui

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.davidsimba.vintbeats.feature.onboarding.OnboardingPreferences
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val prefs: OnboardingPreferences,
    @ApplicationContext private val context: Context,
) : ViewModel() {

    val name: StateFlow<String> = prefs.userName
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "")

    val photoPath: StateFlow<String?> = prefs.photoUri
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val photoVersion: StateFlow<Long> = prefs.photoVersion
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0L)

    val autoDownloadFavorites: StateFlow<Boolean> = prefs.autoDownloadFavorites
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    val equalizerEnabled: StateFlow<Boolean> = prefs.showEqualizer
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    fun setAutoDownloadFavorites(enabled: Boolean) {
        viewModelScope.launch { prefs.setAutoDownloadFavorites(enabled) }
    }

    fun setEqualizerEnabled(enabled: Boolean) {
        viewModelScope.launch { prefs.setShowEqualizer(enabled) }
    }

    fun saveName(name: String) {
        viewModelScope.launch { prefs.setName(name.trim()) }
    }

    fun onImagePicked(uri: Uri) {
        viewModelScope.launch {
            val path = withContext(Dispatchers.IO) {
                runCatching {
                    val file = File(context.filesDir, "profile_photo.jpg")
                    context.contentResolver.openInputStream(uri)?.use { input ->
                        file.outputStream().use { input.copyTo(it) }
                    }
                    file.absolutePath
                }.getOrNull()
            }
            path?.let { prefs.setPhotoUri(it) }
        }
    }
}
