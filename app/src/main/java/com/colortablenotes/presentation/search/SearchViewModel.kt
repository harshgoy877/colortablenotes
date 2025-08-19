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

    private val _searchQuery = MutableStateFlow("")

    val searchResults = _searchQuery
        .debounce(300)
        .filter { it.length >= 2 || it.isEmpty() }
        .flatMapLatest { query ->
            if (query.isEmpty()) {
                flowOf(androidx.paging.PagingData.empty())
            } else {
                repository.searchNotes(query)
            }
        }.cachedIn(viewModelScope)

    fun onEvent(event: SearchEvent) {
        when (event) {
            is SearchEvent.QueryChanged -> {
                _state.update { it.copy(query = event.query) }
                _searchQuery.value = event.query
            }
        }
    }
}
