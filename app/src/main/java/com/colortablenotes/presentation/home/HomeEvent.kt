package com.colortablenotes.presentation.home

sealed class HomeEvent {
    data class CreateNote(val type: String) : HomeEvent()
    object ClearNewNoteCreated : HomeEvent()
    data class SelectNote(val noteId: String) : HomeEvent()
    data class UpdateFilter(val filter: String) : HomeEvent()
    data class UpdateSearchQuery(val query: String) : HomeEvent()
    data class UpdateSorting(val sortBy: String) : HomeEvent()
}
