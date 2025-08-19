package com.colortablenotes.data.local.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo
import androidx.room.Index

@Entity(
    tableName = "checklist_items",
    foreignKeys = [
        ForeignKey(
            entity = Note::class,
            parentColumns = ["id"],
            childColumns = ["note_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["note_id"])] // FIXED: Add index to avoid full table scans
)
data class ChecklistItem(
    @PrimaryKey
    val id: String,

    @ColumnInfo(name = "note_id") // FIXED: Explicitly name the column
    val noteId: String,

    @ColumnInfo(name = "position")
    val position: Int,

    @ColumnInfo(name = "text")
    val text: String,

    @ColumnInfo(name = "is_checked")
    val isChecked: Boolean = false
)
