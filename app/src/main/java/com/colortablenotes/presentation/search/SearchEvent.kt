package com.colortablenotes.presentation.search

sealed class SearchEvent {
    data class UpdateQuery(val query: String) : SearchEvent()
    object Search : SearchEvent()
    object ClearQuery : SearchEvent()
}
