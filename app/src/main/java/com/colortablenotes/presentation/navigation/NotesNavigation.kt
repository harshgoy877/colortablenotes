package com.colortablenotes.presentation.navigation

import androidx.compose.runtime.*
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.colortablenotes.presentation.home.HomeScreen
import com.colortablenotes.presentation.texteditor.TextEditorScreen
import com.colortablenotes.presentation.checklisteditor.ChecklistEditorScreen
import com.colortablenotes.presentation.tableeditor.TableEditorScreen
import com.colortablenotes.presentation.search.SearchScreen

@Composable
fun NotesNavigation(
    handleSharedText: (String) -> Unit = {},
    pendingSharedText: String? = null,
    shouldNavigateToEditor: Boolean = false,
    onSharedTextHandled: () -> Unit = {},
    navController: NavHostController = rememberNavController()
) {
    // Handle shared text navigation
    LaunchedEffect(shouldNavigateToEditor, pendingSharedText) {
        if (shouldNavigateToEditor && pendingSharedText != null) {
            navController.navigate("text_editor/new") {
                launchSingleTop = true
            }
            onSharedTextHandled()
        }
    }

    NavHost(
        navController = navController,
        startDestination = "home"
    ) {
        composable("home") {
            HomeScreen(
                onCreateNote = { type ->
                    when (type) {
                        "TEXT" -> navController.navigate("text_editor/new")
                        "CHECKLIST" -> navController.navigate("checklist_editor/new")
                        "TABLE" -> navController.navigate("table_editor/new")
                    }
                },
                onOpenNote = { noteId, type ->
                    when (type) {
                        "TEXT" -> navController.navigate("text_editor/$noteId")
                        "CHECKLIST" -> navController.navigate("checklist_editor/$noteId")
                        "TABLE" -> navController.navigate("table_editor/$noteId")
                    }
                },
                onSearchClick = {
                    navController.navigate("search")
                }
            )
        }

        composable("search") {
            SearchScreen(
                onBack = { navController.popBackStack() },
                onOpenNote = { noteId, type ->
                    when (type) {
                        "TEXT" -> navController.navigate("text_editor/$noteId") {
                            popUpTo("home")
                        }
                        "CHECKLIST" -> navController.navigate("checklist_editor/$noteId") {
                            popUpTo("home")
                        }
                        "TABLE" -> navController.navigate("table_editor/$noteId") {
                            popUpTo("home")
                        }
                    }
                }
            )
        }

        composable("text_editor/{noteId}") { backStackEntry ->
            val noteId = backStackEntry.arguments?.getString("noteId") ?: "new"
            TextEditorScreen(
                noteId = noteId,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable("checklist_editor/{noteId}") { backStackEntry ->
            val noteId = backStackEntry.arguments?.getString("noteId") ?: "new"
            ChecklistEditorScreen(
                noteId = noteId,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable("table_editor/{noteId}") { backStackEntry ->
            val noteId = backStackEntry.arguments?.getString("noteId") ?: "new"
            TableEditorScreen(
                noteId = noteId,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
