package com.colortablenotes.presentation.tableeditor

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.colortablenotes.data.local.entities.TableCell

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TableEditorScreen(
    noteId: String,
    onNavigateBack: () -> Unit,
    viewModel: TableEditorViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(noteId) {
        viewModel.loadTable(noteId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Table") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.onEvent(TableEvent.Save) }) {
                        Icon(Icons.Default.Save, contentDescription = "Save")
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp)) {
            Row {
                Button(onClick = { viewModel.onEvent(TableEvent.AddRow) }) { Text("Add Row") }
                Spacer(Modifier.width(8.dp))
                Button(onClick = { viewModel.onEvent(TableEvent.AddColumn) }) { Text("Add Col") }
            }
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                itemsIndexed(state.rows) { rowIndex, row ->
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        itemsIndexed(row) { colIndex, cell ->
                            OutlinedTextField(
                                value = cell.text,
                                onValueChange = {
                                    viewModel.onEvent(TableEvent.UpdateCell(cell.copy(text = it)))
                                },
                                modifier = Modifier.width(100.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
