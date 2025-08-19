package com.colortablenotes.data.local.dao

import androidx.room.*
import androidx.paging.PagingSource
import com.colortablenotes.data.local.entities.SearchIndex
import com.colortablenotes.data.local.entities.Note
import kotlinx.coroutines.flow.Flow

@Dao
interface SearchIndexDao {

    @Query("""
        SELECT notes.* FROM search_index 
        JOIN notes ON search_index.note_id = notes.id 
        WHERE search_index MATCH :query 
        ORDER BY 
            notes.pinned DESC,
            notes.updated_at DESC
    """)
    fun searchNotesPaged(query: String): PagingSource<Int, Note>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSearchIndex(searchIndex: SearchIndex)

    @Query("DELETE FROM search_index WHERE note_id = :noteId")
    suspend fun deleteSearchIndex(noteId: String)
}
