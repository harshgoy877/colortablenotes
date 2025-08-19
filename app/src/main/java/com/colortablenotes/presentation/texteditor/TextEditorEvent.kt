package com.colortablenotes.presentation.texteditor

sealed class TextEditorEvent {
    data class UpdateTitle(val title: String) : TextEditorEvent()
    data class UpdateContent(val content: String) : TextEditorEvent()
    object SaveNote : TextEditorEvent()
}
