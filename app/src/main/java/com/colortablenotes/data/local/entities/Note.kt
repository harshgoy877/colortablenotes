package com.colortablenotes.data.local.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "notes")
data class Note(
    @PrimaryKey val id: String,
    @ColumnInfo(name = "type") val type: String, // TEXT, CHECKLIST, TABLE
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "color") val color: String, // Color name or "NONE"
    @ColumnInfo(name = "pinned") val pinned: Boolean = false,
    @ColumnInfo(name = "created_at") val createdAt: Date,
    @ColumnInfo(name = "updated_at") val updatedAt: Date
)
