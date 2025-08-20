package com.colortablenotes.presentation.texteditor

sealed class TextEditorEvent {
    data class UpdateTitle(val title: String) : TextEditorEvent()
    data class UpdateContent(val content: String) : TextEditorEvent()
    data class UpdateColor(val color: String) : TextEditorEvent()
    object TogglePin : TextEditorEvent()
    object SaveNote : TextEditorEvent()
    object DeleteNote : TextEditorEvent()
    object ShareNote : TextEditorEvent()
}

sealed class TextEditorUiEvent {
    data class ShowMessage(val message: String) : TextEditorUiEvent()
    data class ShowError(val message: String) : TextEditorUiEvent()
    data class ShareText(val text: String) : TextEditorUiEvent()
    object NavigateBack : TextEditorUiEvent()
}
