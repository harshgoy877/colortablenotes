package com.colortablenotes.presentation.tableeditor

sealed class TableEditorEvent {
    data class UpdateTitle(val title: String) : TableEditorEvent()
    data class UpdateCell(val row: Int, val col: Int, val text: String) : TableEditorEvent()
    object AddRow : TableEditorEvent()
    object AddColumn : TableEditorEvent()
    data class DeleteRow(val row: Int) : TableEditorEvent()
    data class DeleteColumn(val col: Int) : TableEditorEvent()
    object SaveNote : TableEditorEvent()
}
