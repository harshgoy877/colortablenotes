package com.colortablenotes.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import com.colortablenotes.data.local.entities.Note
import com.colortablenotes.data.repository.NotesRepository
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val notesRepository: NotesRepository
) : ViewModel() {

    private val _state = MutableStateFlow(HomeState())
    val state: StateFlow<HomeState> = _state.asStateFlow()

    private val _uiEvent = MutableSharedFlow<HomeUiEvent>()
    val uiEvent: SharedFlow<HomeUiEvent> = _uiEvent.asSharedFlow()

    val notes: Flow<PagingData<Note>> = notesRepository
        .getNotesPaged(
            noteType = _state.value.selectedNoteType,
            sortBy = _state.value.sortOrder
        )
        .cachedIn(viewModelScope)

    val pinnedNotes: StateFlow<List<Note>> = notesRepository
        .getPinnedNotes()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun onEvent(event: HomeEvent) {
        when (event) {
            is HomeEvent.ChangeNoteType -> {
                _state.value = _state.value.copy(selectedNoteType = event.noteType)
            }
            is HomeEvent.ChangeSortOrder -> {
                _state.value = _state.value.copy(sortOrder = event.sortOrder)
            }
            is HomeEvent.ToggleSelectionMode -> {
                _state.value = _state.value.copy(
                    isSelectionMode = !_state.value.isSelectionMode,
                    selectedNotes = if (_state.value.isSelectionMode) emptySet() else _state.value.selectedNotes
                )
            }
            is HomeEvent.SelectNote -> {
                val selectedNotes = _state.value.selectedNotes.toMutableSet()
                if (selectedNotes.contains(event.noteId)) {
                    selectedNotes.remove(event.noteId)
                } else {
                    selectedNotes.add(event.noteId)
                }
                _state.value = _state.value.copy(selectedNotes = selectedNotes)
            }
            is HomeEvent.DeleteSelectedNotes -> {
                deleteSelectedNotes()
            }
            is HomeEvent.PinNote -> {
                pinNote(event.noteId)
            }
            is HomeEvent.UnpinNote -> {
                unpinNote(event.noteId)
            }
            HomeEvent.ClearSelection -> {
                _state.value = _state.value.copy(
                    isSelectionMode = false,
                    selectedNotes = emptySet()
                )
            }
        }
    }

    private fun deleteSelectedNotes() {
        viewModelScope.launch {
            val selectedNotes = _state.value.selectedNotes
            var successCount = 0
            var failureCount = 0

            selectedNotes.forEach { noteId ->
                notesRepository.deleteNote(noteId).fold(
                    onSuccess = { successCount++ },
                    onFailure = { failureCount++ }
                )
            }

            _state.value = _state.value.copy(
                isSelectionMode = false,
                selectedNotes = emptySet()
            )

            if (successCount > 0) {
                _uiEvent.emit(HomeUiEvent.ShowMessage("$successCount notes deleted"))
            }
            if (failureCount > 0) {
                _uiEvent.emit(HomeUiEvent.ShowError("Failed to delete $failureCount notes"))
            }
        }
    }

    private fun pinNote(noteId: String) {
        viewModelScope.launch {
            try {
                val note = notesRepository.getNoteById(noteId)
                if (note != null) {
                    notesRepository.updateNote(note.copy(pinned = true)).fold(
                        onSuccess = { _uiEvent.emit(HomeUiEvent.ShowMessage("Note pinned")) },
                        onFailure = { _uiEvent.emit(HomeUiEvent.ShowError("Failed to pin note")) }
                    )
                }
            } catch (e: Exception) {
                _uiEvent.emit(HomeUiEvent.ShowError("Failed to pin note"))
            }
        }
    }

    private fun unpinNote(noteId: String) {
        viewModelScope.launch {
            try {
                val note = notesRepository.getNoteById(noteId)
                if (note != null) {
                    notesRepository.updateNote(note.copy(pinned = false)).fold(
                        onSuccess = { _uiEvent.emit(HomeUiEvent.ShowMessage("Note unpinned")) },
                        onFailure = { _uiEvent.emit(HomeUiEvent.ShowError("Failed to unpin note")) }
                    )
                }
            } catch (e: Exception) {
                _uiEvent.emit(HomeUiEvent.ShowError("Failed to unpin note"))
            }
        }
    }
}
