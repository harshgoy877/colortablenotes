package com.colortablenotes.presentation.texteditor

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TextEditorScreen(
    noteId: String,
    onNavigateBack: () -> Unit,
    viewModel: TextEditorViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusRequester = remember { FocusRequester() }

    var showColorPicker by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    LaunchedEffect(noteId) {
        viewModel.loadNote(noteId)
    }

    LaunchedEffect(Unit) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                is TextEditorUiEvent.ShowMessage -> {
                    // Show snackbar
                }
                is TextEditorUiEvent.ShowError -> {
                    // Show error snackbar
                }
                is TextEditorUiEvent.ShareText -> {
                    // Share text via intent
                }
                TextEditorUiEvent.NavigateBack -> {
                    onNavigateBack()
                }
            }
        }
    }

    if (state.isLoading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = androidx.compose.ui.Alignment.Center
        ) {
            CircularProgressIndicator()
        }
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(if (state.isNewNote) "New Note" else "Edit Note")
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    // Pin/Unpin
                    IconButton(
                        onClick = { viewModel.onEvent(TextEditorEvent.TogglePin) }
                    ) {
                        Icon(
                            imageVector = if (state.isPinned) Icons.Default.PushPin else Icons.Default.PushPin,
                            contentDescription = if (state.isPinned) "Unpin" else "Pin",
                            tint = if (state.isPinned) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    // Color picker
                    IconButton(onClick = { showColorPicker = true }) {
                        Icon(Icons.Default.Palette, contentDescription = "Change color")
                    }

                    // Share
                    IconButton(
                        onClick = { viewModel.onEvent(TextEditorEvent.ShareNote) }
                    ) {
                        Icon(Icons.Default.Share, contentDescription = "Share")
                    }

                    // More options
                    var showMenu by remember { mutableStateOf(false) }
                    IconButton(onClick = { showMenu = true }) {
                        Icon(Icons.Default.MoreVert, contentDescription = "More options")
                    }

                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Delete") },
                            onClick = {
                                showMenu = false
                                showDeleteDialog = true
                            },
                            leadingIcon = {
                                Icon(
                                    Icons.Default.Delete,
                                    null,
                                    tint = MaterialTheme.colorScheme.error
                                )
                            }
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.onEvent(TextEditorEvent.SaveNote) },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                if (state.isSaving) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                } else {
                    Icon(Icons.Default.Save, contentDescription = "Save")
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            // Title field
            OutlinedTextField(
                value = state.title,
                onValueChange = { viewModel.onEvent(TextEditorEvent.UpdateTitle(it)) },
                label = { Text("Title") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Sentences,
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = { focusRequester.requestFocus() }
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Content field
            OutlinedTextField(
                value = state.content,
                onValueChange = { viewModel.onEvent(TextEditorEvent.UpdateContent(it)) },
                label = { Text("Write your thoughts...") },
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .focusRequester(focusRequester),
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.Sentences
                )
            )

            // Unsaved changes indicator
            if (state.hasUnsavedChanges) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "You have unsaved changes",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }

    // Color picker dialog
    if (showColorPicker) {
        AlertDialog(
            onDismissRequest = { showColorPicker = false },
            title = { Text("Choose Color") },
            text = {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        // No color option
                        Surface(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape),
                            onClick = {
                                viewModel.onEvent(TextEditorEvent.UpdateColor("NONE"))
                                showColorPicker = false
                            },
                            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                            border = if (state.selectedColor == "NONE") {
                                androidx.compose.foundation.BorderStroke(
                                    2.dp,
                                    MaterialTheme.colorScheme.primary
                                )
                            } else null
                        ) {
                            Icon(
                                Icons.Default.Block,
                                contentDescription = "No color",
                                modifier = Modifier.padding(12.dp)
                            )
                        }
                    }

                    items(viewModel.getAvailableColors()) { (colorName, color) ->
                        Surface(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape),
                            onClick = {
                                viewModel.onEvent(TextEditorEvent.UpdateColor(colorName))
                                showColorPicker = false
                            },
                            color = color,
                            border = if (state.selectedColor == colorName) {
                                androidx.compose.foundation.BorderStroke(
                                    3.dp,
                                    MaterialTheme.colorScheme.primary
                                )
                            } else null
                        ) {}
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showColorPicker = false }) {
                    Text("Done")
                }
            }
        )
    }

    // Delete confirmation dialog
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Note") },
            text = { Text("Are you sure you want to delete this note? This action cannot be undone.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteDialog = false
                        viewModel.onEvent(TextEditorEvent.DeleteNote)
                    }
                ) {
                    Text("Delete", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
