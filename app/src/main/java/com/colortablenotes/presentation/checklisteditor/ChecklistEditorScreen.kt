package com.colortablenotes.presentation.checklisteditor

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckBox
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.colortablenotes.data.local.entities.ChecklistItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChecklistEditorScreen(
    noteId: String,
    onNavigateBack: () -> Unit,
    viewModel: ChecklistEditorViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(noteId) {
        viewModel.loadChecklistItems(noteId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Checklist") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.onEvent(ChecklistEvent.Save) }) {
                        Icon(Icons.Default.Save, contentDescription = "Save")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            itemsIndexed(state.items) { index, item ->
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = item.isChecked,
                        onCheckedChange = { checked ->
                            viewModel.onEvent(ChecklistEvent.ToggleItem(item.copy(isChecked = checked)))
                        }
                    )
                    Spacer(Modifier.width(8.dp))
                    TextField(
                        value = item.text,
                        onValueChange = { text ->
                            viewModel.onEvent(ChecklistEvent.UpdateText(item.copy(text = text)))
                        },
                        modifier = Modifier.weight(1f),
                        placeholder = { Text("Item ${index + 1}") }
                    )
                    IconButton(onClick = { viewModel.onEvent(ChecklistEvent.Delete(item)) }) {
                        Icon(Icons.Default.CheckBox, contentDescription = "Delete")
                    }
                }
            }
            item {
                TextButton(onClick = { viewModel.onEvent(ChecklistEvent.Add) }) {
                    Icon(Icons.Default.CheckBox, contentDescription = "Add")
                    Spacer(Modifier.width(4.dp))
                    Text("Add Item")
                }
            }
        }
    }
}
