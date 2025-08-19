package com.colortablenotes.data.local.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo

@Entity(
    tableName = "table_cells",
    foreignKeys = [
        ForeignKey(
            entity = Note::class,
            parentColumns = ["id"],
            childColumns = ["note_id"], // This matches the column name below
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class TableCell(
    @PrimaryKey
    val id: String,

    @ColumnInfo(name = "note_id") // FIXED: Explicitly name the column
    val noteId: String,

    @ColumnInfo(name = "row_index")
    val rowIndex: Int,

    @ColumnInfo(name = "col_index")
    val colIndex: Int,

    @ColumnInfo(name = "text")
    val text: String
)
