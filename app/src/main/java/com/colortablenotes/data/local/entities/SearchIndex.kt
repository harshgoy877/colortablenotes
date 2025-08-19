package com.colortablenotes.data.local.entities

import androidx.room.Entity
import androidx.room.Fts4
import androidx.room.ColumnInfo

@Fts4
@Entity(tableName = "search_index")
data class SearchIndex(
    @ColumnInfo(name = "note_id")
    val noteId: String,

    @ColumnInfo(name = "title")
    val title: String,

    @ColumnInfo(name = "text_body")
    val textBody: String?,

    @ColumnInfo(name = "checklist_joined")
    val checklistJoined: String?,

    @ColumnInfo(name = "table_flattened")
    val tableFlattened: String?
)
