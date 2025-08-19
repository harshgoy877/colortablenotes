package com.colortablenotes.presentation.texteditor

data class TextEditorState(
    val noteId: String = "",
    val title: String = "",
    val content: String = "",
    val isLoading: Boolean = false,
    val hasUnsavedChanges: Boolean = false
)
