package com.colortablenotes.presentation.tableeditor

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.colortablenotes.data.repository.NotesRepository
import com.colortablenotes.data.local.entities.TableCell
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.UUID  // ADDED: UUID import here
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
            val flatCells = repository.getTableCells(noteId)

            // Build rows from flat list
            val maxRow = flatCells.maxByOrNull { it.rowIndex }?.rowIndex ?: 2
            val maxCol = flatCells.maxByOrNull { it.colIndex }?.colIndex ?: 2
            val rows = (0..maxRow).map { r ->
                (0..maxCol).map { c ->
                    flatCells.find { it.rowIndex == r && it.colIndex == c }
                        ?: cellAt(r, c, noteId)
                }
            }

            _state.update { current ->
                current.copy(
                    title = note?.title ?: "",
                    rows = rows,
                    isLoading = false
                )
            }
        }
    }

    fun onEvent(event: TableEditorEvent) {
        when (event) {
            TableEditorEvent.Save -> saveTable()
            TableEditorEvent.AddRow -> addRow()
            TableEditorEvent.AddColumn -> addColumn()
            is TableEditorEvent.UpdateCell -> updateCell(event.rowIndex, event.colIndex, event.text)
        }
    }

    // UPDATED: Fixed addRow function
    private fun addRow() {
        _state.update { current ->
            val newRowIndex = current.rows.size
            val newRow = current.rows.first().mapIndexed { col, cell ->
                cell.copy(
                    id = UUID.randomUUID().toString(),
                    rowIndex = newRowIndex,
                    noteId = current.noteId
                )
            }
            // FIXED: Wrap newRow in listOf()
            current.copy(
                rows = current.rows + listOf(newRow),
                hasUnsavedChanges = true
            )
        }
    }

    // UPDATED: Fixed addColumn function
    private fun addColumn() {
        _state.update { current ->
            val newColIndex = current.rows.first().size
            val updatedRows = current.rows.mapIndexed { r, row ->
                row + cellAt(r, newColIndex, current.noteId)
            }
            // FIXED: updatedRows is already List<List<TableCell>>
            current.copy(
                rows = updatedRows,
                hasUnsavedChanges = true
            )
        }
    }

    private fun updateCell(rowIndex: Int, colIndex: Int, text: String) {
        _state.update { current ->
            val updatedRows = current.rows.mapIndexed { r, row ->
                if (r == rowIndex) {
                    row.mapIndexed { c, cell ->
                        if (c == colIndex) cell.copy(text = text) else cell
                    }
                } else row
            }
            current.copy(rows = updatedRows, hasUnsavedChanges = true)
        }
    }

    private fun saveTable() = viewModelScope.launch {
        val allCells = state.value.rows.flatten()
        repository.insertTableCells(allCells)
        _state.update { it.copy(hasUnsavedChanges = false) }
    }

    // ADDED: Helper function to create new cells
    private fun cellAt(rowIndex: Int, colIndex: Int, noteId: String) = TableCell(
        id = UUID.randomUUID().toString(),
        noteId = noteId,
        rowIndex = rowIndex,
        colIndex = colIndex,
        text = ""
    )
}
