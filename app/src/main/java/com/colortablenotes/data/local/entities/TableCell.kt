package com.colortablenotes.data.local.entities  // CORRECT


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
            childColumns = ["note_id"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class TableCell(
    @PrimaryKey
    val id: String,

    @ColumnInfo
    val noteId: String,

    val rowIndex: Int,

    val colIndex: Int,

    val text: String
)
