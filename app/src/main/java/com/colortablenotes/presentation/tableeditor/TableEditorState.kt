package com.colortablenotes.presentation.tableeditor

import com.colortablenotes.data.local.entities.TableCell
import java.util.UUID

data class TableEditorState(
    val noteId: String = "",
    val title: String = "",
    // Initialize a 3x3 grid of empty cells
    val rows: List<List<TableCell>> = List(3) { row ->
        List(3) { col ->
            TableCell(
                id = UUID.randomUUID().toString(),
                noteId = noteId,
                rowIndex = row,
                colIndex = col,
                text = ""
            )
        }
    },
    val isLoading: Boolean = false,
    val hasUnsavedChanges: Boolean = false
)
