package com.colortablenotes.data.local.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo
import androidx.room.Index

@Entity(
    tableName = "text_notes",
    foreignKeys = [
        ForeignKey(
            entity = Note::class,
            parentColumns = ["id"],
            childColumns = ["note_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["note_id"])]
)
data class TextNote(
    @PrimaryKey
    @ColumnInfo(name = "note_id")
    val noteId: String,

    @ColumnInfo(name = "body")
    val body: String
)
