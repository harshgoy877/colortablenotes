package com.colortablenotes.presentation.home

import androidx.paging.PagingData
import com.colortablenotes.data.local.entities.Note
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

data class HomeState(
    val notes: Flow<PagingData<Note>> = flowOf(PagingData.empty()),
    val searchQuery: String = "",
    val selectedFilter: String = "All",
    val selectedSorting: String = "LAST_EDITED",
    val selectedNotes: Set<String> = emptySet(),
    val notesCount: Int = 0,
    val notesLimitReached: Boolean = false,
    val newNoteCreated: Pair<String, String>? = null
)
