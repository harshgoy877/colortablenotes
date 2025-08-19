package com.colortablenotes.presentation.home

data class HomeState(
    val selectedNoteId: String? = null,
    val newlyCreatedNoteId: String? = null,
    val currentFilter: String = "ALL",
    val searchQuery: String = "",
    val sortBy: String = "LAST_EDITED",
    val isLoading: Boolean = false
)
