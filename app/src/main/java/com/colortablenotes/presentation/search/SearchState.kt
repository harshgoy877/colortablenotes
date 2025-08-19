package com.colortablenotes.presentation.search

import androidx.paging.PagingData
import com.colortablenotes.data.local.entities.Note
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

data class SearchState(
    val searchQuery: String = "",
    val searchResults: Flow<PagingData<Note>> = flowOf(PagingData.empty()),
    val isSearching: Boolean = false
)
