package com.colortablenotes.data.local.dao

import androidx.paging.PagingSource
import androidx.room.*
import com.colortablenotes.data.local.entities.Note
import com.colortablenotes.data.local.entities.TextNote
import com.colortablenotes.data.local.entities.ChecklistItem
import com.colortablenotes.data.local.entities.TableCell
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao {

    @Query("""
        SELECT * FROM notes 
        WHERE (:noteType IS NULL OR type = :noteType)
        ORDER BY 
            CASE WHEN :sortBy = 'LAST_EDITED' THEN updated_at END DESC,
            CASE WHEN :sortBy = 'TITLE_AZ' THEN title END ASC,
            pinned DESC
    """)
    fun getNotesPaged(
        noteType: String? = null,
        sortBy: String = "LAST_EDITED"
    ): PagingSource<Int, Note>

    @Query("SELECT * FROM notes WHERE pinned = 1 ORDER BY updated_at DESC LIMIT 5")
    fun getPinnedNotes(): Flow<List<Note>>

    @Query("SELECT * FROM notes WHERE id = :noteId")
    suspend fun getNoteById(noteId: String): Note?

    @Query("SELECT COUNT(*) FROM notes")
    suspend fun getNotesCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: Note): Long

    @Update
    suspend fun updateNote(note: Note)

    @Delete
    suspend fun deleteNote(note: Note)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTextNote(textNote: TextNote)

    @Query("SELECT * FROM text_notes WHERE note_id = :noteId")
    suspend fun getTextNote(noteId: String): TextNote?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChecklistItems(items: List<ChecklistItem>)

    @Query("SELECT * FROM checklist_items WHERE note_id = :noteId ORDER BY position")
    suspend fun getChecklistItems(noteId: String): List<ChecklistItem>

    @Query("DELETE FROM checklist_items WHERE note_id = :noteId")
    suspend fun deleteChecklistItems(noteId: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTableCells(cells: List<TableCell>)

    @Query("SELECT * FROM table_cells WHERE note_id = :noteId ORDER BY row_index, col_index")
    suspend fun getTableCells(noteId: String): List<TableCell>

    @Query("DELETE FROM table_cells WHERE note_id = :noteId")
    suspend fun deleteTableCells(noteId: String)
}
