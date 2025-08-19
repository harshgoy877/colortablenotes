package com.colortablenotes.presentation.tableeditor

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.colortablenotes.data.repository.NotesRepository
import com.colortablenotes.data.local.entities.TableCell
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.UUID // ADDED: Missing UUID import
import javax.inject.Inject

@HiltViewModel
class TableEditorViewModel @Inject constructor(
    private val repository: NotesRepository
) : ViewModel() {

    private val _state = MutableStateFlow(TableEditorState())
    val state: StateFlow<TableEditorState> = _state.asStateFlow()

    fun loadNote(noteId: String) {
        viewModelScope.launch {
            _state.update { it.copy(noteId = noteId, isLoading = true) }

            val note = repository.getNoteById(noteId)
            val tableCells = repository.getTableCells(noteId)

            _state.update { currentState ->
                currentState.copy(
                    title = note?.title ?: "",
                    cells = tableCells,
                    isLoading = false
                )
            }
        }
    }

    fun addRow() {
        viewModelScope.launch {
            val currentState = _state.value
            val newRowIndex = currentState.cells.maxByOrNull { it.rowIndex }?.rowIndex?.plus(1) ?: 0
            val newCells = (0 until currentState.columnCount).map { colIndex ->
                TableCell(
                    id = UUID.randomUUID().toString(), // FIXED: Now UUID is imported
                    noteId = currentState.noteId,
                    rowIndex = newRowIndex,
                    colIndex = colIndex,
                    text = ""
                )
            }

            _state.update { it.copy(cells = it.cells + newCells) }
        }
    }

    fun addColumn() {
        viewModelScope.launch {
            val currentState = _state.value
            val newColIndex = currentState.columnCount
            val newCells = (0 until currentState.rowCount).map { rowIndex ->
                TableCell(
                    id = UUID.randomUUID().toString(), // FIXED: Now UUID is imported
                    noteId = currentState.noteId,
                    rowIndex = rowIndex,
                    colIndex = newColIndex,
                    text = ""
                )
            }

            _state.update {
                it.copy(
                    cells = it.cells + newCells,
                    columnCount = newColIndex + 1
                )
            }
        }
    }

    fun updateCell(rowIndex: Int, colIndex: Int, text: String) {
        viewModelScope.launch {
            val currentState = _state.value
            val cellToUpdate = currentState.cells.find {
                it.rowIndex == rowIndex && it.colIndex == colIndex
            }

            if (cellToUpdate != null) {
                val updatedCell = cellToUpdate.copy(text = text)
                val updatedCells = currentState.cells.map { cell ->
                    if (cell.id == cellToUpdate.id) updatedCell else cell
                }
                _state.update { it.copy(cells = updatedCells, hasUnsavedChanges = true) }
            } else {
                // Create new cell
                val newCell = TableCell(
                    id = UUID.randomUUID().toString(), // FIXED: Now UUID is imported
                    noteId = currentState.noteId,
                    rowIndex = rowIndex,
                    colIndex = colIndex,
                    text = text
                )
                _state.update {
                    it.copy(
                        cells = it.cells + newCell,
                        hasUnsavedChanges = true
                    )
                }
            }
        }
    }

    fun saveTable() {
        viewModelScope.launch {
            val currentState = _state.value
            repository.insertTableCells(currentState.cells)
            _state.update { it.copy(hasUnsavedChanges = false) }
        }
    }
}

data class TableEditorState(
    val noteId: String = "",
    val title: String = "",
    val cells: List<TableCell> = emptyList(),
    val rowCount: Int = 3,
    val columnCount: Int = 3,
    val isLoading: Boolean = false,
    val hasUnsavedChanges: Boolean = false
)
