package com.colortablenotes.presentation.checklisteditor

sealed class ChecklistEditorEvent {
    data class UpdateTitle(val title: String) : ChecklistEditorEvent()
    data class AddItem(val text: String) : ChecklistEditorEvent()
    data class UpdateItem(val id: String, val text: String) : ChecklistEditorEvent()
    data class ToggleItem(val id: String) : ChecklistEditorEvent()
    data class DeleteItem(val id: String) : ChecklistEditorEvent()
    data class ReorderItems(val fromIndex: Int, val toIndex: Int) : ChecklistEditorEvent()
    object SaveNote : ChecklistEditorEvent()
}
