package com.colortablenotes.presentation.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.colortablenotes.data.repository.NotesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val repository: NotesRepository
) : ViewModel() {

    private val _state = MutableStateFlow(SearchState())
    val state: StateFlow<SearchState> = _state.asStateFlow()

    fun onEvent(event: SearchEvent) {
        when (event) {
            is SearchEvent.UpdateQuery -> updateQuery(event.query)
            SearchEvent.ClearQuery -> clearQuery()
        }
    }

    private fun updateQuery(query: String) {
        _state.update { it.copy(searchQuery = query) }
        if (query.length >= 2) {
            performSearch(query)
        }
    }

    private fun clearQuery() {
        _state.update { it.copy(searchQuery = "", searchResults = flowOf()) }
    }

    private fun performSearch(query: String) {
        // TODO: Implement actual search when SearchIndexDao is ready
        viewModelScope.launch {
            repository.getNotesPaged()
                .cachedIn(viewModelScope)
                .collect { pagingData ->
                    _state.update { it.copy(searchResults = flowOf(pagingData)) }
                }
        }
    }
}
