package com.colortables.local.entities

import androidx.room.Entity
import androidx.room.Fts4
import androidx.room.ColumnInfo

@Fts4
@Entity(tableName = "search_index")
data class SearchIndex(
    @ColumnInfo
    val noteId: String,

    val title: String,

    val textBody: String?,

    val checklistJoined: String?,

    val tableFlattened: String?
)
