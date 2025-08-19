package com.colortablenotes.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.colortablenotes.presentation.checklisteditor.ChecklistEditorScreen
import com.colortablenotes.presentation.home.HomeScreen
import com.colortablenotes.presentation.search.SearchScreen
import com.colortablenotes.presentation.tableeditor.TableEditorScreen
import com.colortablenotes.presentation.texteditor.TextEditorScreen

@Composable
fun NotesNavigation(
    handleSharedText: (String) -> Unit = {}
) {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        // Home Screen
        composable(Screen.Home.route) {
            HomeScreen(
                onNavigateToEditor = { noteId, noteType ->
                    when (noteType) {
                        "TEXT" -> navController.navigate(Screen.TextEditor.withArgs(noteId))
                        "CHECKLIST" -> navController.navigate(Screen.ChecklistEditor.withArgs(noteId))
                        "TABLE" -> navController.navigate(Screen.TableEditor.withArgs(noteId))
                    }
                },
                onNavigateToSearch = { navController.navigate(Screen.Search.route) }
            )
        }

        // Search Screen
        composable(Screen.Search.route) {
            SearchScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToEditor = { noteId, noteType ->
                    when (noteType) {
                        "TEXT" -> navController.navigate(Screen.TextEditor.withArgs(noteId))
                        "CHECKLIST" -> navController.navigate(Screen.ChecklistEditor.withArgs(noteId))
                        "TABLE" -> navController.navigate(Screen.TableEditor.withArgs(noteId))
                    }
                }
            )
        }

        // Text Editor Screen
        composable(Screen.TextEditor.route) { backStackEntry ->
            val noteId = backStackEntry.arguments?.getString("noteId") ?: ""
            TextEditorScreen(
                noteId = noteId,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // Checklist Editor Screen
        composable(Screen.ChecklistEditor.route) { backStackEntry ->
            val noteId = backStackEntry.arguments?.getString("noteId") ?: ""
            ChecklistEditorScreen(
                noteId = noteId,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // Table Editor Screen
        composable(Screen.TableEditor.route) { backStackEntry ->
            val noteId = backStackEntry.arguments?.getString("noteId") ?: ""
            TableEditorScreen(
                noteId = noteId,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // Settings Screen (to be implemented)
        composable(Screen.Settings.route) {
            // TODO: SettingsScreen()
        }
    }
}
