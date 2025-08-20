package com.colortablenotes.presentation.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import androidx.paging.compose.itemContentType
import androidx.hilt.navigation.compose.hiltViewModel
import com.colortablenotes.presentation.components.NoteCard
import com.colortablenotes.presentation.components.SpeedDialFAB
import com.colortablenotes.presentation.components.EmptyStateComponent
import com.colortablenotes.presentation.components.LoadingComponent

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
    val pinnedNotes by viewModel.pinnedNotes.collectAsState()
    val context = LocalContext.current

    // Collect UI events
    LaunchedEffect(Unit) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                is HomeUiEvent.ShowMessage -> {
                    // Show snackbar message
                }
                is HomeUiEvent.ShowError -> {
                    // Show error snackbar
                }
                is HomeUiEvent.NavigateToEditor -> {
                    onOpenNote(event.noteId, event.noteType)
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            "ColorTable Notes",
                            style = MaterialTheme.typography.headlineSmall.copy(
                                fontWeight = FontWeight.Bold
                            )
                        )
                        if (state.isSelectionMode) {
                            Text(
                                "${state.selectedNotes.size} selected",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                },
                actions = {
                    if (state.isSelectionMode) {
                        IconButton(
                            onClick = { viewModel.onEvent(HomeEvent.DeleteSelectedNotes) }
                        ) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete selected")
                        }
                        IconButton(
                            onClick = { viewModel.onEvent(HomeEvent.ClearSelection) }
                        ) {
                            Icon(Icons.Default.Close, contentDescription = "Clear selection")
                        }
                    } else {
                        IconButton(onClick = onSearchClick) {
                            Icon(Icons.Default.Search, contentDescription = "Search")
                        }

                        // Filter menu
                        var showFilterMenu by remember { mutableStateOf(false) }
                        IconButton(onClick = { showFilterMenu = true }) {
                            Icon(Icons.Default.FilterList, contentDescription = "Filter")
                        }

                        DropdownMenu(
                            expanded = showFilterMenu,
                            onDismissRequest = { showFilterMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("All Notes") },
                                onClick = {
                                    viewModel.onEvent(HomeEvent.ChangeNoteType(null))
                                    showFilterMenu = false
                                },
                                leadingIcon = { Icon(Icons.Default.Notes, null) }
                            )
                            DropdownMenuItem(
                                text = { Text("Text Notes") },
                                onClick = {
                                    viewModel.onEvent(HomeEvent.ChangeNoteType("TEXT"))
                                    showFilterMenu = false
                                },
                                leadingIcon = { Icon(Icons.Default.Description, null) }
                            )
                            DropdownMenuItem(
                                text = { Text("Checklists") },
                                onClick = {
                                    viewModel.onEvent(HomeEvent.ChangeNoteType("CHECKLIST"))
                                    showFilterMenu = false
                                },
                                leadingIcon = { Icon(Icons.Default.CheckBox, null) }
                            )
                            DropdownMenuItem(
                                text = { Text("Tables") },
                                onClick = {
                                    viewModel.onEvent(HomeEvent.ChangeNoteType("TABLE"))
                                    showFilterMenu = false
                                },
                                leadingIcon = { Icon(Icons.Default.GridOn, null) }
                            )
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            if (!state.isSelectionMode) {
                SpeedDialFAB(
                    onCreateTextNote = { onCreateNote("TEXT") },
                    onCreateChecklistNote = { onCreateNote("CHECKLIST") },
                    onCreateTableNote = { onCreateNote("TABLE") }
                )
            }
        }
    ) { paddingValues ->

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            // Pinned notes section
            if (pinnedNotes.isNotEmpty()) {
                item {
                    Text(
                        text = "Pinned",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.SemiBold
                        ),
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }

                item {
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(horizontal = 4.dp)
                    ) {
                        items(pinnedNotes, key = { it.id }) { note ->
                            NoteCard(
                                note = note,
                                onClick = { onOpenNote(note.id, note.type) },
                                onLongClick = {
                                    if (!state.isSelectionMode) {
                                        viewModel.onEvent(HomeEvent.ToggleSelectionMode)
                                    }
                                    viewModel.onEvent(HomeEvent.SelectNote(note.id))
                                },
                                modifier = Modifier.width(280.dp),
                                isSelected = state.selectedNotes.contains(note.id)
                            )
                        }
                    }
                }

                item {
                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 16.dp),
                        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                    )
                }

                item {
                    Text(
                        text = "All Notes",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.SemiBold
                        ),
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
            }

            // All notes section
            if (notes.itemCount == 0) {
                item {
                    EmptyStateComponent(
                        title = "No notes yet",
                        subtitle = "Tap the + button to create your first note",
                        icon = Icons.Default.Note
                    )
                }
            } else {
                items(
                    count = notes.itemCount,
                    key = notes.itemKey { it.id },
                    contentType = notes.itemContentType { "NoteItem" }
                ) { index ->
                    val note = notes[index]
                    if (note != null) {
                        NoteCard(
                            note = note,
                            onClick = {
                                if (state.isSelectionMode) {
                                    viewModel.onEvent(HomeEvent.SelectNote(note.id))
                                } else {
                                    onOpenNote(note.id, note.type)
                                }
                            },
                            onLongClick = {
                                if (!state.isSelectionMode) {
                                    viewModel.onEvent(HomeEvent.ToggleSelectionMode)
                                }
                                viewModel.onEvent(HomeEvent.SelectNote(note.id))
                            },
                            isSelected = state.selectedNotes.contains(note.id)
                        )
                    }
                }
            }
        }
    }
}
