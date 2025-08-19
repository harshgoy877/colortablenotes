package com.colortablenotes.presentation.tableeditor

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.colortablenotes.data.local.entities.TableCell
import com.colortablenotes.data.repository.NotesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.max

@HiltViewModel
class TableEditorViewModel @Inject constructor(
    private val repository: NotesRepository
) : ViewModel() {

    private val _state = MutableStateFlow(TableState())
    val state: StateFlow<TableState> = _state.asStateFlow()

    fun loadTable(noteId: String) {
        viewModelScope.launch {
            val cells = repository.getTableCells(noteId)
            val maxRow = (cells.maxOfOrNull { it.rowIndex } ?: -1) + 1
            val maxCol = (cells.maxOfOrNull { it.colIndex } ?: -1) + 1
            val rows = List(maxRow) { row ->
                MutableList(maxCol) { col ->
                    cells.find { it.rowIndex == row && it.colIndex == col }
                        ?: TableCell(UUID.randomUUID().toString(), noteId, row, col, "")
                }
            }
            _state.update { it.copy(noteId = noteId, rows = rows.toMutableList()) }
        }
    }

    fun onEvent(event: TableEvent) {
        when (event) {
            TableEvent.AddRow -> addRow()
            TableEvent.AddColumn -> addColumn()
            is TableEvent.UpdateCell -> updateCell(event.cell)
            TableEvent.Save -> saveTable()
        }
    }

    private fun addRow() {
        _state.update { state ->
            val newRowIndex = state.rows.size
            val cols = state.rows.firstOrNull()?.size ?: 0
            val newRow = MutableList(cols) { col ->
                TableCell(UUID.randomUUID().toString(), state.noteId, newRowIndex, col, "")
            }
            state.rows.add(newRow)
            state.copy(rows = state.rows)
        }
    }

    private fun addColumn() {
        _state.update { state ->
            val newColIndex = (state.rows.firstOrNull()?.size ?: 0)
            state.rows.forEachIndexed { rowIndex, row ->
                row.add(
                    TableCell(UUID.randomUUID().toString(), state.noteId, rowIndex, newColIndex, "")
                )
            }
            state.copy(rows = state.rows)
        }
    }

    private fun updateCell(cell: TableCell) {
        _state.update { state ->
            state.rows[cell.rowIndex][cell.colIndex] = cell
            state.copy(rows = state.rows)
        }
    }

    private fun saveTable() {
        viewModelScope.launch {
            repository.deleteTableCells(_state.value.noteId)
            repository.insertTableCells(_state.value.rows.flatten())
        }
    }
}
