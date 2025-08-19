package com.colortablenotes.presentation.checklisteditor

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.colortablenotes.data.repository.NotesRepository
import com.colortablenotes.data.local.entities.ChecklistItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class ChecklistEditorViewModel @Inject constructor(
    private val repository: NotesRepository
) : ViewModel() {

    private val _state = MutableStateFlow(ChecklistEditorState())
    val state: StateFlow<ChecklistEditorState> = _state.asStateFlow()

    fun loadNote(noteId: String) {
        viewModelScope.launch {
            if (noteId == "new") {
                // Create a new checklist note
                val result = repository.createNote("CHECKLIST", "New Checklist")
                result.onSuccess { newNoteId ->
                    _state.update {
                        it.copy(
                            noteId = newNoteId,
                            title = "New Checklist",
                            items = emptyList(),
                            isLoading = false
                        )
                    }
                }
            } else {
                // Load existing note
                _state.update { it.copy(noteId = noteId, isLoading = true) }

                val note = repository.getNoteById(noteId)
                val items = repository.getChecklistItems(noteId)

                _state.update { currentState ->
                    currentState.copy(
                        title = note?.title ?: "",
                        items = items,
                        isLoading = false
                    )
                }
            }
        }
    }

    fun updateTitle(title: String) {
        _state.update { it.copy(title = title, hasUnsavedChanges = true) }
    }

    fun addItem() {
        val newItem = ChecklistItem(
            id = UUID.randomUUID().toString(),
            noteId = _state.value.noteId,
            position = _state.value.items.size,
            text = "",
            isChecked = false
        )
        _state.update {
            it.copy(
                items = it.items + newItem,
                hasUnsavedChanges = true
            )
        }
    }

    fun updateItem(index: Int, text: String) {
        _state.update { currentState ->
            val updatedItems = currentState.items.mapIndexed { i, item ->
                if (i == index) item.copy(text = text) else item // FIXED: Lambda parameter issue
            }
            currentState.copy(items = updatedItems, hasUnsavedChanges = true)
        }
    }

    fun toggleItem(index: Int) {
        _state.update { currentState ->
            val updatedItems = currentState.items.mapIndexed { i, item ->
                if (i == index) item.copy(isChecked = !item.isChecked) else item // FIXED: Lambda parameter issue
            }
            currentState.copy(items = updatedItems, hasUnsavedChanges = true)
        }
    }

    fun removeItem(index: Int) {
        _state.update { currentState ->
            val updatedItems = currentState.items.filterIndexed { i, _ -> i != index }
                .mapIndexed { newIndex, item -> item.copy(position = newIndex) }
            currentState.copy(items = updatedItems, hasUnsavedChanges = true)
        }
    }

    fun saveChecklist() {
        viewModelScope.launch {
            val currentState = _state.value
            repository.insertChecklistItems(currentState.items)
            _state.update { it.copy(hasUnsavedChanges = false) }
        }
    }
}

data class ChecklistEditorState(
    val noteId: String = "",
    val title: String = "",
    val items: List<ChecklistItem> = emptyList(),
    val isLoading: Boolean = false,
    val hasUnsavedChanges: Boolean = false
)
