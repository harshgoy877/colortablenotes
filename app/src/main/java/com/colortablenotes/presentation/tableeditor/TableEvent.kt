package com.colortablenotes.presentation.tableeditor

import com.colortablenotes.data.local.entities.TableCell

sealed class TableEvent {
    object AddRow : TableEvent()
    object AddColumn : TableEvent()
    data class UpdateCell(val cell: TableCell) : TableEvent()
    object Save : TableEvent()
}
