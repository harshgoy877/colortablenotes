package com.colortablenotes.presentation.tableeditor

import com.colortablenotes.data.local.entities.TableCell

data class TableState(
    val noteId: String = "",
    val rows: MutableList<MutableList<TableCell>> = mutableListOf()
)
