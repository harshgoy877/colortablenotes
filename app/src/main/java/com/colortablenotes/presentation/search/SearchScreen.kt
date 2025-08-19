package com.colortablenotes.presentation.search

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import androidx.paging.compose.itemContentType
import androidx.hilt.navigation.compose.hiltViewModel
import com.colortablenotes.presentation.components.NoteCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    onBack: () -> Unit,
    onOpenNote: (String, String) -> Unit,
    viewModel: SearchViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val searchResults = viewModel.searchResults.collectAsLazyPagingItems()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    OutlinedTextField(
                        value = state.searchQuery,
                        onValueChange = { viewModel.onEvent(SearchEvent.QueryChanged(it)) },
                        placeholder = { Text("Search notes...") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        trailingIcon = {
                            if (state.searchQuery.isNotEmpty()) {
                                IconButton(onClick = { viewModel.onEvent(SearchEvent.QueryChanged("")) }) {
                                    Icon(Icons.Filled.Clear, contentDescription = "Clear search")
                                }
                            }
                        }
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(
                count = searchResults.itemCount,
                key = searchResults.itemKey { it.id },
                contentType = searchResults.itemContentType { "SearchResultItem" }
            ) { index ->
                val note = searchResults[index]
                if (note != null) {
                    NoteCard(
                        note = note,
                        onClick = { onOpenNote(note.id, note.type) },
                        onLongClick = { /* Handle selection */ }
                    )
                }
            }
        }
    }
}
