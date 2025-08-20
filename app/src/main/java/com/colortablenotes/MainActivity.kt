package com.colortablenotes

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import com.colortablenotes.data.repository.NotesRepository
import com.colortablenotes.presentation.navigation.NotesNavigation
import com.colortablenotes.presentation.theme.ColorTableNotesTheme
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var notesRepository: NotesRepository

    private var pendingSharedText by mutableStateOf<String?>(null)
    private var shouldNavigateToEditor by mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            ColorTableNotesTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    NotesNavigation(
                        handleSharedText = ::handleSharedText,
                        pendingSharedText = pendingSharedText,
                        shouldNavigateToEditor = shouldNavigateToEditor,
                        onSharedTextHandled = {
                            pendingSharedText = null
                            shouldNavigateToEditor = false
                        }
                    )
                }
            }
        }

        handleIntent(intent)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent?) {
        when (intent?.action) {
            Intent.ACTION_SEND -> {
                if (intent.type == "text/plain") {
                    val sharedText = intent.getStringExtra(Intent.EXTRA_TEXT)
                    if (!sharedText.isNullOrBlank()) {
                        handleSharedText(sharedText)
                    }
                }
            }
        }
    }

    private fun handleSharedText(text: String) {
        // Create a new text note with shared content
        lifecycleScope.launch {
            try {
                val result = notesRepository.createNote(
                    type = "TEXT",
                    title = extractTitleFromText(text),
                    color = "BLUE"
                )

                result.onSuccess { noteId ->
                    // Save the text content
                    notesRepository.saveTextNote(noteId, text)

                    // Set the pending shared text and trigger navigation
                    pendingSharedText = text
                    shouldNavigateToEditor = true
                }
            } catch (e: Exception) {
                // Handle error - could show a toast or snackbar
                e.printStackTrace()
            }
        }
    }

    private fun extractTitleFromText(text: String): String {
        // Extract first line or first 50 characters as title
        val firstLine = text.lines().firstOrNull()?.trim() ?: ""
        return when {
            firstLine.length <= 50 -> firstLine.ifEmpty { "Shared Note" }
            else -> firstLine.take(47) + "..."
        }
    }
}
