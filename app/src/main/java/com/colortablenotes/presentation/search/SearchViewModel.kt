package com.colortablenotes.presentation.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import com.colortablenotes.data.local.entities.Note
import com.colortablenotes.data.repository.NotesRepository
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val notesRepository: NotesRepository
) : ViewModel() {

    private val _state = MutableStateFlow(SearchState())
    val state: StateFlow<SearchState> = _state.asStateFlow()

    val searchResults: Flow<PagingData<Note>> = _state
        .map { it.query }
        .distinctUntilChanged()
        .debounce(300)
        .filter { it.isNotBlank() && it.length >= 2 }
        .flatMapLatest { query ->
            _state.value = _state.value.copy(isSearching = true)
            notesRepository.searchNotes(query)
        }
        .onEach {
            _state.value = _state.value.copy(isSearching = false)
        }
        .cachedIn(viewModelScope)

    fun onEvent(event: SearchEvent) {
        when (event) {
            is SearchEvent.UpdateQuery -> {
                _state.value = _state.value.copy(query = event.query)
            }
            SearchEvent.Search -> {
                // Search is automatically triggered by the flow
            }
            SearchEvent.ClearQuery -> {
                _state.value = _state.value.copy(query = "", isSearching = false)
            }
        }
    }
}
