package com.colortablenotes.presentation.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.paging.compose.collectAsLazyPagingItems
import com.colortablenotes.data.local.entities.Note
import com.colortablenotes.presentation.components.NoteCard
import com.colortablenotes.presentation.components.SpeedDialFAB

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToEditor: (String, String) -> Unit,
    onNavigateToSearch: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val notes = state.notes.collectAsLazyPagingItems()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(id = com.colortablenotes.R.string.app_name)) },
                actions = {
                    IconButton(onClick = onNavigateToSearch) {
                        Icon(Icons.Default.Search, contentDescription = null)
                    }
                    IconButton(onClick = { /* TODO: Settings */ }) {
                        Icon(Icons.Default.Settings, contentDescription = null)
                    }
                }
            )
        },
        floatingActionButton = {
            SpeedDialFAB(
                onCreateTextNote = { viewModel.onEvent(HomeEvent.CreateNote("TEXT")) },
                onCreateChecklistNote = { viewModel.onEvent(HomeEvent.CreateNote("CHECKLIST")) },
                onCreateTableNote = { viewModel.onEvent(HomeEvent.CreateNote("TABLE")) }
            )
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            LazyColumn(
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(notes) { note: Note? ->
                    note?.let {
                        NoteCard(
                            note = it,
                            onClick = { onNavigateToEditor(it.id, it.type) },
                            onLongClick = { /* TODO: Handle selection */ }
                        )
                    }
                }
            }
        }
    }
}
