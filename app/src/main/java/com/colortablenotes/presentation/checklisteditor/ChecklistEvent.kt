package com.colortablenotes.presentation.checklisteditor

import com.colortablenotes.data.local.entities.ChecklistItem

sealed class ChecklistEvent {
    object Add : ChecklistEvent()
    data class UpdateText(val item: ChecklistItem) : ChecklistEvent()
    data class ToggleItem(val item: ChecklistItem) : ChecklistEvent()
    data class Delete(val item: ChecklistItem) : ChecklistEvent()
    object Save : ChecklistEvent()
}
