package com.colortablenotes.presentation.checklisteditor

import com.colortablenotes.data.local.entities.ChecklistItem

data class ChecklistState(
    val noteId: String = "",
    val items: MutableList<ChecklistItem> = mutableListOf()
)
