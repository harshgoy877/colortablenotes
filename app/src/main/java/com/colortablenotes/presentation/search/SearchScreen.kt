package com.colortablenotes.presentation.search

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import com.colortablenotes.presentation.components.NoteCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    onNavigateBack: () -> Unit,
    onNavigateToEditor: (String, String) -> Unit,
    viewModel: SearchViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val searchResults = state.searchResults.collectAsLazyPagingItems()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    OutlinedTextField(
                        value = state.searchQuery,
                        onValueChange = { viewModel.onEvent(SearchEvent.UpdateQuery(it)) },
                        placeholder = { Text("Search notes...") },
                        singleLine = true,
                        trailingIcon = {
                            if (state.searchQuery.isNotEmpty()) {
                                IconButton(
                                    onClick = { viewModel.onEvent(SearchEvent.ClearQuery) }
                                ) {
                                    Icon(Icons.Default.Clear, contentDescription = "Clear")
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier.padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(searchResults) { note ->
                note?.let {
                    NoteCard(
                        note = it,
                        onClick = { onNavigateToEditor(it.id, it.type) },
                        onLongClick = { /* TODO */ },
                        searchQuery = state.searchQuery
                    )
                }
            }
        }
    }
}
