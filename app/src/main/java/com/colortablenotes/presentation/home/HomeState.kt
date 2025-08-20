package com.colortablenotes.presentation.home

data class HomeState(
    val selectedNoteType: String? = null, // null = all, "TEXT", "CHECKLIST", "TABLE"
    val sortOrder: String = "LAST_EDITED", // "LAST_EDITED", "TITLE_AZ", "CREATED"
    val isSelectionMode: Boolean = false,
    val selectedNotes: Set<String> = emptySet(),
    val isLoading: Boolean = false,
    val error: String? = null
)
