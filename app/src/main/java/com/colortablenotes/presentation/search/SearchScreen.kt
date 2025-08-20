package com.colortablenotes.presentation.search

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import androidx.paging.compose.itemContentType
import com.colortablenotes.presentation.components.NoteCard
import com.colortablenotes.presentation.components.EmptyStateComponent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    onBack: () -> Unit,
    onOpenNote: (String, String) -> Unit,
    viewModel: SearchViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val searchResults = viewModel.searchResults.collectAsLazyPagingItems()
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    OutlinedTextField(
                        value = state.query,
                        onValueChange = { viewModel.onEvent(SearchEvent.UpdateQuery(it)) },
                        placeholder = { Text("Search notes...") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .focusRequester(focusRequester),
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                        keyboardActions = KeyboardActions(
                            onSearch = {
                                keyboardController?.hide()
                                viewModel.onEvent(SearchEvent.Search)
                            }
                        ),
                        trailingIcon = {
                            if (state.query.isNotEmpty()) {
                                IconButton(
                                    onClick = { viewModel.onEvent(SearchEvent.ClearQuery) }
                                ) {
                                    Icon(Icons.Default.Clear, contentDescription = "Clear")
                                }
                            }
                        }
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
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
            when {
                state.query.isEmpty() -> {
                    item {
                        EmptyStateComponent(
                            title = "Search your notes",
                            subtitle = "Type in the search box above to find your notes",
                            icon = Icons.Default.Search
                        )
                    }
                }
                state.isSearching -> {
                    item {
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(modifier = Modifier.padding(32.dp))
                        }
                    }
                }
                searchResults.itemCount == 0 && !state.isSearching -> {
                    item {
                        EmptyStateComponent(
                            title = "No results found",
                            subtitle = "Try different keywords or check your spelling",
                            icon = Icons.Default.SearchOff
                        )
                    }
                }
                else -> {
                    items(
                        count = searchResults.itemCount,
                        key = searchResults.itemKey { it.id },
                        contentType = searchResults.itemContentType { "SearchResult" }
                    ) { index ->
                        val note = searchResults[index]
                        if (note != null) {
                            NoteCard(
                                note = note,
                                onClick = { onOpenNote(note.id, note.type) },
                                onLongClick = { /* Handle long click */ },
                                searchQuery = state.query
                            )
                        }
                    }
                }
            }
        }
    }
}
