package com.colortablenotes.presentation.search

data class SearchState(
    val query: String = "",
    val isSearching: Boolean = false,
    val error: String? = null
)
