package com.colortablenotes.presentation.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import androidx.paging.compose.itemContentType
import androidx.hilt.navigation.compose.hiltViewModel
import com.colortablenotes.presentation.components.NoteCard
import com.colortablenotes.presentation.components.SpeedDialFAB

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onCreateNote: (String) -> Unit,
    onOpenNote: (String, String) -> Unit,
    onSearchClick: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val notes = viewModel.notes.collectAsLazyPagingItems()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("ColorTable Notes") },
                actions = {
                    IconButton(onClick = onSearchClick) {
                        Icon(Icons.Default.Search, contentDescription = "Search")
                    }
                }
            )
        },
        floatingActionButton = {
            SpeedDialFAB(
                onCreateText = { onCreateNote("TEXT") },
                onCreateChecklist = { onCreateNote("CHECKLIST") },
                onCreateTable = { onCreateNote("TABLE") }
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
            // FIXED: Use proper paging items syntax
            items(
                count = notes.itemCount,
                key = notes.itemKey { it.id },
                contentType = notes.itemContentType { "NoteItem" }
            ) { index ->
                val note = notes[index]
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
