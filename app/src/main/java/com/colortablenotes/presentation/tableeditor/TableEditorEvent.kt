package com.colortablenotes.presentation.tableeditor

sealed class TableEditorEvent {
    object Save : TableEditorEvent()
    object AddRow : TableEditorEvent()
    object AddColumn : TableEditorEvent()
    data class UpdateCell(val rowIndex: Int, val colIndex: Int, val text: String) : TableEditorEvent()
}
