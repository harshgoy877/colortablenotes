package com.colortablenotes.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
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

    val notes = repository.getNotesPaged().cachedIn(viewModelScope)

    fun onEvent(event: HomeEvent) {
        when (event) {
            is HomeEvent.CreateNote -> createNote(event.type)
            HomeEvent.ClearNewNoteCreated -> clearNewNoteCreated() // ADDED: Missing branch
            is HomeEvent.SelectNote -> selectNote(event.noteId) // ADDED: Missing branch
            is HomeEvent.UpdateFilter -> updateFilter(event.filter) // ADDED: Missing branch
            is HomeEvent.UpdateSearchQuery -> updateSearchQuery(event.query) // ADDED: Missing branch
            is HomeEvent.UpdateSorting -> updateSorting(event.sortBy) // ADDED: Missing branch
        }
    }

    private fun createNote(type: String) {
        viewModelScope.launch {
            val result = repository.createNote(type, "New Note")
            result.onSuccess { noteId ->
                _state.update { it.copy(newlyCreatedNoteId = noteId) }
            }
        }
    }

    // ADDED: All missing functions for the when branches
    private fun clearNewNoteCreated() {
        _state.update { it.copy(newlyCreatedNoteId = null) }
    }

    private fun selectNote(noteId: String) {
        _state.update { it.copy(selectedNoteId = noteId) }
    }

    private fun updateFilter(filter: String) {
        _state.update { it.copy(currentFilter = filter) }
    }

    private fun updateSearchQuery(query: String) {
        _state.update { it.copy(searchQuery = query) }
    }

    private fun updateSorting(sortBy: String) {
        _state.update { it.copy(sortBy = sortBy) }
    }
}
