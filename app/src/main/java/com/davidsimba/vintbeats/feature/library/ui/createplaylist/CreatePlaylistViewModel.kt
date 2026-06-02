package com.davidsimba.vintbeats.feature.library.ui.createplaylist

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.davidsimba.vintbeats.feature.library.domain.PlaylistRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class CreatePlaylistViewModel @Inject constructor(
    private val repository: PlaylistRepository,
    @ApplicationContext private val context: Context,
) : ViewModel() {

    var name by mutableStateOf("")
        private set

    var coverImagePath by mutableStateOf<String?>(null)
        private set

    fun onNameChange(value: String) {
        name = value
    }

    fun onImagePicked(uri: Uri) {
        viewModelScope.launch {
            val path = withContext(Dispatchers.IO) {
                val dir = File(context.filesDir, "playlist_covers").also { it.mkdirs() }
                val dest = File(dir, "${UUID.randomUUID()}.jpg")
                context.contentResolver.openInputStream(uri)?.use { input ->
                    dest.outputStream().use { output -> input.copyTo(output) }
                }
                dest.absolutePath
            }
            coverImagePath = path
        }
    }

    fun create(onCreated: (Int) -> Unit) {
        if (name.isBlank()) return
        viewModelScope.launch {
            val id = repository.createPlaylist(name.trim(), coverImagePath)
            onCreated(id)
        }
    }
}
