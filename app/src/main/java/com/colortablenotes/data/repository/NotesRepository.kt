package com.colornotes.data.repository

import androidx.paging.*
import kotlinx.coroutines.flow.*
import javax.inject.Inject
import javax.inject.Singleton
import com.colortablenotes.data.repository
import com.colortablenotes.data.repository
import com.colortablenotes.data.repository
import com.colortablenotes.data.repository
import com.colortablenotes.data.repository
import com.colortablenotes.data.repository
import com.colortablenotes.data.repository
import com.colortablenotes.data.repository
import com.colortablenotes.data.repository
import com.colortablenotes.data.repository

@Singleton
class NotesRepository @Inject constructor(
    private val noteDao: NoteDao,
    private val searchDao: SearchDao,
    private val database: NotesDatabase
) {

    companion object {
        private const val PAGE_SIZE = 20
        private const val MAX_NOTES = NotesDatabase.MAX_NOTES_LIMIT
    }

    fun getNotesPaged(
        noteType: String? = null,
        sortBy: String = "LAST_EDITED"
    ): Flow<PagingData<Note>> {
        return Pager(
            config = PagingConfig(
                pageSize = PAGE_SIZE,
                prefetchDistance = 5,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { noteDao.getNotesPaged(noteType, sortBy) }
        ).flow
    }

    fun searchNotes(query: String): Flow<PagingData<Note>> {
        return Pager(
            config = PagingConfig(
                pageSize = PAGE_SIZE,
                prefetchDistance = 5,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                searchDao.searchNotes(query)
            }
        ).flow
    }

    fun getPinnedNotes(): Flow<List<Note>> = noteDao.getPinnedNotes()

    suspend fun getNoteById(noteId: String): Note? =
        noteDao.getNoteById(noteId)

    suspend fun createNote(
        type: String,
        title: String,
        color: String = "NONE"
    ): Result<String> {
        return try {
            val currentCount = noteDao.getNotesCount()
            if (currentCount >= MAX_NOTES) {
                return Result.failure(Exception("Maximum note limit ($MAX_NOTES) reached"))
            }

            val noteId = UUID.randomUUID().toString()
            val now = Date()
            val note = Note(
                id = noteId,
                type = type,
                title = title,
                color = color,
                createdAt = now,
                updatedAt = now
            )

            noteDao.insertNote(note)
            updateSearchIndex(noteId)
            Result.success(noteId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateNote(note: Note): Result<Unit> {
        return try {
            val updatedNote = note.copy(updatedAt = Date())
            noteDao.updateNote(updatedNote)
            updateSearchIndex(note.id)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteNote(noteId: String): Result<Unit> {
        return try {
            val note = noteDao.getNoteById(noteId)
            if (note != null) {
                noteDao.deleteNote(note)
                searchDao.deleteSearchIndex(noteId)
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun saveTextNote(noteId: String, body: String): Result<Unit> {
        return try {
            val textNote = TextNote(noteId = noteId, body = body)
            noteDao.insertTextNote(textNote)
            updateSearchIndex(noteId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getTextNote(noteId: String): TextNote? =
        noteDao.getTextNote(noteId)

    suspend fun insertChecklistItems(items: List<ChecklistItem>) =
        noteDao.insertChecklistItems(items)

    suspend fun getChecklistItems(noteId: String): List<ChecklistItem> =
        noteDao.getChecklistItems(noteId)

    suspend fun deleteChecklistItems(noteId: String) =
        noteDao.deleteChecklistItems(noteId)

    suspend fun insertTableCells(cells: List<TableCell>) =
        noteDao.insertTableCells(cells)

    suspend fun getTableCells(noteId: String): List<TableCell> =
        noteDao.getTableCells(noteId)

    suspend fun deleteTableCells(noteId: String) =
        noteDao.deleteTableCells(noteId)

    private suspend fun updateSearchIndex(noteId: String) {
        val note = noteDao.getNoteById(noteId) ?: return

        val textBody = when (note.type) {
            "TEXT" -> noteDao.getTextNote(noteId)?.body
            else -> null
        }

        val checklistJoined = when (note.type) {
            "CHECKLIST" -> noteDao.getChecklistItems(noteId)
                .joinToString(" ") { it.text }
            else -> null
        }

        val tableFlattened = when (note.type) {
            "TABLE" -> noteDao.getTableCells(noteId)
                .joinToString(" ") { it.text }
            else -> null
        }

        val searchIndex = SearchIndex(
            noteId = note.id,
            title = note.title,
            textBody = textBody,
            checklistJoined = checklistJoined,
            tableFlattened = tableFlattened
        )

        searchDao.insertSearchIndex(searchIndex)
    }
}
