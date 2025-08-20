package com.colortablenotes.presentation.texteditor

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import com.colortablenotes.data.repository.NotesRepository
import com.colortablenotes.presentation.theme.getNoteColors
import javax.inject.Inject

@HiltViewModel
class TextEditorViewModel @Inject constructor(
    private val notesRepository: NotesRepository
) : ViewModel() {

    private val _state = MutableStateFlow(TextEditorState())
    val state: StateFlow<TextEditorState> = _state.asStateFlow()

    private val _uiEvent = MutableSharedFlow<TextEditorUiEvent>()
    val uiEvent: SharedFlow<TextEditorUiEvent> = _uiEvent.asSharedFlow()

    private var currentNoteId: String? = null

    fun loadNote(noteId: String) {
        if (noteId == "new") {
            // Create new note
            currentNoteId = null
            _state.value = _state.value.copy(
                isLoading = false,
                isNewNote = true
            )
        } else {
            // Load existing note
            currentNoteId = noteId
            _state.value = _state.value.copy(isLoading = true)

            viewModelScope.launch {
                try {
                    val note = notesRepository.getNoteById(noteId)
                    val textNote = notesRepository.getTextNote(noteId)

                    if (note != null) {
                        _state.value = _state.value.copy(
                            title = note.title,
                            content = textNote?.body ?: "",
                            selectedColor = note.color,
                            isPinned = note.pinned,
                            isLoading = false,
                            isNewNote = false
                        )
                    } else {
                        _uiEvent.emit(TextEditorUiEvent.ShowError("Note not found"))
                        _uiEvent.emit(TextEditorUiEvent.NavigateBack)
                    }
                } catch (e: Exception) {
                    _state.value = _state.value.copy(isLoading = false)
                    _uiEvent.emit(TextEditorUiEvent.ShowError("Failed to load note"))
                }
            }
        }
    }

    fun onEvent(event: TextEditorEvent) {
        when (event) {
            is TextEditorEvent.UpdateTitle -> {
                _state.value = _state.value.copy(
                    title = event.title,
                    hasUnsavedChanges = true
                )
            }
            is TextEditorEvent.UpdateContent -> {
                _state.value = _state.value.copy(
                    content = event.content,
                    hasUnsavedChanges = true
                )
            }
            is TextEditorEvent.UpdateColor -> {
                _state.value = _state.value.copy(
                    selectedColor = event.color,
                    hasUnsavedChanges = true
                )
            }
            TextEditorEvent.TogglePin -> {
                _state.value = _state.value.copy(
                    isPinned = !_state.value.isPinned,
                    hasUnsavedChanges = true
                )
            }
            TextEditorEvent.SaveNote -> {
                saveNote()
            }
            TextEditorEvent.DeleteNote -> {
                deleteNote()
            }
            TextEditorEvent.ShareNote -> {
                shareNote()
            }
        }
    }

    private fun saveNote() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isSaving = true)

            try {
                val currentState = _state.value
                val noteId = currentNoteId ?: generateNewNoteId()

                if (currentNoteId == null) {
                    // Create new note
                    val result = notesRepository.createNote(
                        type = "TEXT",
                        title = currentState.title.ifBlank { "Untitled" },
                        color = currentState.selectedColor
                    )

                    result.fold(
                        onSuccess = { createdNoteId ->
                            currentNoteId = createdNoteId
                            saveTextContent(createdNoteId, currentState.content)
                        },
                        onFailure = { error ->
                            _state.value = _state.value.copy(isSaving = false)
                            _uiEvent.emit(TextEditorUiEvent.ShowError("Failed to create note"))
                        }
                    )
                } else {
                    // Update existing note
                    val existingNote = notesRepository.getNoteById(noteId)
                    if (existingNote != null) {
                        val updatedNote = existingNote.copy(
                            title = currentState.title.ifBlank { "Untitled" },
                            color = currentState.selectedColor,
                            pinned = currentState.isPinned
                        )

                        notesRepository.updateNote(updatedNote).fold(
                            onSuccess = {
                                saveTextContent(noteId, currentState.content)
                            },
                            onFailure = {
                                _state.value = _state.value.copy(isSaving = false)
                                _uiEvent.emit(TextEditorUiEvent.ShowError("Failed to update note"))
                            }
                        )
                    }
                }
            } catch (e: Exception) {
                _state.value = _state.value.copy(isSaving = false)
                _uiEvent.emit(TextEditorUiEvent.ShowError("Failed to save note"))
            }
        }
    }

    private suspend fun saveTextContent(noteId: String, content: String) {
        notesRepository.saveTextNote(noteId, content).fold(
            onSuccess = {
                _state.value = _state.value.copy(
                    isSaving = false,
                    hasUnsavedChanges = false,
                    isNewNote = false
                )
                _uiEvent.emit(TextEditorUiEvent.ShowMessage("Note saved"))
            },
            onFailure = {
                _state.value = _state.value.copy(isSaving = false)
                _uiEvent.emit(TextEditorUiEvent.ShowError("Failed to save note content"))
            }
        )
    }

    private fun deleteNote() {
        val noteId = currentNoteId ?: return

        viewModelScope.launch {
            notesRepository.deleteNote(noteId).fold(
                onSuccess = {
                    _uiEvent.emit(TextEditorUiEvent.ShowMessage("Note deleted"))
                    _uiEvent.emit(TextEditorUiEvent.NavigateBack)
                },
                onFailure = {
                    _uiEvent.emit(TextEditorUiEvent.ShowError("Failed to delete note"))
                }
            )
        }
    }

    private fun shareNote() {
        val currentState = _state.value
        val shareText = buildString {
            append(currentState.title)
            if (currentState.content.isNotBlank()) {
                append("\n\n")
                append(currentState.content)
            }
        }

        if (shareText.isNotBlank()) {
            viewModelScope.launch {
                _uiEvent.emit(TextEditorUiEvent.ShareText(shareText))
            }
        }
    }

    private fun generateNewNoteId(): String {
        return java.util.UUID.randomUUID().toString()
    }

    fun getAvailableColors() = getNoteColors()
}
