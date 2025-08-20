package com.colortablenotes.presentation.tableeditor

data class TableEditorState(
    val title: String = "",
    val rows: Int = 3,
    val columns: Int = 3,
    val cells: Map<Pair<Int, Int>, String> = emptyMap(),
    val isLoading: Boolean = false,
    val error: String? = null
)
