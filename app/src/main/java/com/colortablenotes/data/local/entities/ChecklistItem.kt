package com.colortables.local.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo

@Entity(
    tableName = "checklist_items",
    foreignKeys = [
        ForeignKey(
            entity = Note::class,
            parentColumns = ["id"],
            childColumns = ["note_id"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class ChecklistItem(
    @PrimaryKey
    val id: String,

    @ColumnInfo
    val noteId: String,

    val position: Int,

    val text: String,

    val isChecked: Boolean = false
)
