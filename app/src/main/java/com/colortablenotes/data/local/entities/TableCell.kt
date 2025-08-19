package com.colortablenotes.data.local.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo
import androidx.room.Index

@Entity(
    tableName = "table_cells",
    foreignKeys = [
        ForeignKey(
            entity = Note::class,
            parentColumns = ["id"],
            childColumns = ["note_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["note_id"])] // ADDED: Index to fix the warning
)
data class TableCell(
    @PrimaryKey
    val id: String,

    @ColumnInfo(name = "note_id")
    val noteId: String,

    @ColumnInfo(name = "row_index")
    val rowIndex: Int,

    @ColumnInfo(name = "col_index")
    val colIndex: Int,

    @ColumnInfo(name = "text")
    val text: String
)
