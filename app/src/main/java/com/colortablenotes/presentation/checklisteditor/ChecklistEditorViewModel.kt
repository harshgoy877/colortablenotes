package com.colortablenotes.presentation.checklisteditor

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.colortablenotes.data.repository.NotesRepository
import com.colortablenotes.data.local.entities.ChecklistItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject
import java.util.*

@HiltViewModel
class ChecklistEditorViewModel @Inject constructor(
    private val repository: NotesRepository
) : ViewModel() {

    private val _state = MutableStateFlow(ChecklistState())
    val state: StateFlow<ChecklistState> = _state.asStateFlow()

    fun loadChecklistItems(noteId: String) {
        viewModelScope.launch {
            val items = repository.getChecklistItems(noteId)
            _state.update { it.copy(noteId = noteId, items = items.toMutableList()) }
        }
    }

    fun onEvent(event: ChecklistEvent) {
        when (event) {
            is ChecklistEvent.Add -> addItem()
            is ChecklistEvent.UpdateText -> updateText(event.item)
            is ChecklistEvent.ToggleItem -> toggleItem(event.item)
            is ChecklistEvent.Delete -> deleteItem(event.item)
            ChecklistEvent.Save -> saveItems()
        }
    }

    private fun addItem() {
        _state.update {
            val newList = it.items.toMutableList()
            newList.add(ChecklistItem(UUID.randomUUID().toString(), it.noteId, newList.size, ""))
            it.copy(items = newList)
        }
    }

    private fun updateText(item: ChecklistItem) {
        _state.update { state ->
            state.copy(items = state.items.map { if (it.id == item.id) item else it }.toMutableList())
        }
    }

    private fun toggleItem(item: ChecklistItem) {
        updateText(item)
    }

    private fun deleteItem(item: ChecklistItem) {
        _state.update { state ->
            val newList = state.items.filterNot { it.id == item.id }.toMutableList()
            it.copy(items = newList)
        }
    }

    private fun saveItems() {
        viewModelScope.launch {
            repository.deleteChecklistItems(_state.value.noteId)
            repository.insertChecklistItems(_state.value.items)
        }
    }
}
