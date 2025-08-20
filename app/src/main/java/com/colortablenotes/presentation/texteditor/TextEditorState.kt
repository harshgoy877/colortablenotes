package com.colortablenotes.presentation.texteditor

data class TextEditorState(
    val title: String = "",
    val content: String = "",
    val selectedColor: String = "NONE",
    val isPinned: Boolean = false,
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val hasUnsavedChanges: Boolean = false,
    val isNewNote: Boolean = true,
    val error: String? = null
)
