package com.colortablenotes.presentation.search

sealed class SearchEvent {
    data class QueryChanged(val query: String) : SearchEvent()
}
