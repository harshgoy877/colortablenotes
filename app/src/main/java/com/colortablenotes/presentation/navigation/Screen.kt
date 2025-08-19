package com.colortablenotes.presentation.navigation

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Search : Screen("search")
    object Settings : Screen("settings")
    object TextEditor : Screen("text_editor/{noteId}")
    object ChecklistEditor : Screen("checklist_editor/{noteId}")
    object TableEditor : Screen("table_editor/{noteId}")

    fun withArgs(vararg args: String): String {
        return buildString {
            append(route)
            args.forEach { arg -> append("/$arg") }
        }
    }
}
