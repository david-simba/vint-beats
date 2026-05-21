package com.davidsimba.vintbeats.feature.library.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.davidsimba.vintbeats.feature.cassette.domain.CassetteRepository
import com.davidsimba.vintbeats.feature.cassette.domain.SavedCassette
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class LibraryViewModel @Inject constructor(
    repository: CassetteRepository
) : ViewModel() {

    val cassettes: StateFlow<List<SavedCassette>> = repository.getAllCassettes()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
}
