package com.colortablenotes.presentation.checklisteditor

data class ChecklistEditorState(
    val title: String = "",
    val items: List<ChecklistItemState> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

data class ChecklistItemState(
    val id: String,
    val text: String,
    val isChecked: Boolean,
    val position: Int
)
