package com.colortablenotes.presentation.texteditor

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.colortablenotes.data.repository.NotesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TextEditorViewModel @Inject constructor(
    private val repository: NotesRepository
) : ViewModel() {

    private val _state = MutableStateFlow(TextEditorState())
    val state: StateFlow<TextEditorState> = _state.asStateFlow()

    fun loadNote(noteId: String) {
        viewModelScope.launch {
            if (noteId == "new") {
                // Create a new text note
                val result = repository.createNote("TEXT", "New Text Note")
                result.onSuccess { newNoteId ->
                    _state.update {
                        it.copy(
                            noteId = newNoteId,
                            title = "New Text Note",
                            content = "",
                            isLoading = false
                        )
                    }
                }
            } else {
                // Load existing note
                _state.update { it.copy(noteId = noteId, isLoading = true) }

                val note = repository.getNoteById(noteId)
                val textNote = repository.getTextNote(noteId)

                _state.update { currentState ->
                    currentState.copy(
                        title = note?.title ?: "",
                        content = textNote?.body ?: "",
                        isLoading = false
                    )
                }
            }
        }
    }

    fun onEvent(event: TextEditorEvent) {
        when (event) {
            is TextEditorEvent.UpdateTitle -> updateTitle(event.title)
            is TextEditorEvent.UpdateContent -> updateContent(event.content)
            TextEditorEvent.SaveNote -> saveNote()
        }
    }

    private fun updateTitle(title: String) {
        _state.update { it.copy(title = title, hasUnsavedChanges = true) }
    }

    private fun updateContent(content: String) {
        _state.update { it.copy(content = content, hasUnsavedChanges = true) }
    }

    private fun saveNote() {
        viewModelScope.launch {
            val currentState = _state.value
            if (currentState.noteId.isNotEmpty()) {
                repository.saveTextNote(currentState.noteId, currentState.content)
                _state.update { it.copy(hasUnsavedChanges = false) }
            }
        }
    }
}
