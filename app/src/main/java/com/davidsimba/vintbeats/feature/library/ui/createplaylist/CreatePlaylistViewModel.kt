package com.davidsimba.vintbeats.feature.library.ui.createplaylist

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.davidsimba.vintbeats.feature.library.domain.playlist.PlaylistRepository
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
    savedStateHandle: SavedStateHandle,
    private val repository: PlaylistRepository,
    @ApplicationContext private val context: Context,
) : ViewModel() {

    private val editPlaylistId: Int? = savedStateHandle.get<Int>("playlistId")?.takeIf { it != -1 }
    val isEditMode: Boolean get() = editPlaylistId != null

    var name by mutableStateOf("")
        private set

    var coverImagePath by mutableStateOf<String?>(null)
        private set

    init {
        editPlaylistId?.let { id ->
            viewModelScope.launch {
                val info = repository.getPlaylistInfo(id) ?: return@launch
                name = info.name
                coverImagePath = info.coverImagePath
            }
        }
    }

    fun onNameChange(value: String) { name = value }

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

    fun save(onDone: (Int) -> Unit) {
        if (name.isBlank()) return
        viewModelScope.launch {
            if (editPlaylistId != null) {
                repository.updatePlaylist(editPlaylistId, name.trim(), coverImagePath)
                onDone(editPlaylistId)
            } else {
                val id = repository.createPlaylist(name.trim(), coverImagePath)
                onDone(id)
            }
        }
    }
}
