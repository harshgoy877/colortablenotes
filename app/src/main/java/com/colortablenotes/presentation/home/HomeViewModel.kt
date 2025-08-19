package com.colortablenotes.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.colortablenotes.data.local.entities.Note
import com.colortablenotes.data.repository.NotesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: NotesRepository
) : ViewModel() {

    private val _state = MutableStateFlow(HomeState())
    val state: StateFlow<HomeState> = _state.asStateFlow()

    init { loadNotes() }

    fun onEvent(event: HomeEvent) {
        when (event) {
            is HomeEvent.CreateNote -> createNote(event.type)
        }
    }

    private fun loadNotes() {
        viewModelScope.launch {
            repository.getNotesPaged()
                .cachedIn(viewModelScope)
                .collect { pagingData ->
                    _state.update { it.copy(notes = flowOf(pagingData)) }
                }
        }
    }

    private fun createNote(type: String) {
        viewModelScope.launch {
            repository.createNote(type, "New Note").onSuccess { noteId ->
                _state.update { it.copy(newNoteCreated = noteId to type) }
            }
        }
    }
}
