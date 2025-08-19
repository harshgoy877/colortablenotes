package com.colortablenotes.data.local.database

import androidx.room.*
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.colortablenotes.data.local.dao.NoteDao
import com.colortablenotes.data.local.dao.SearchIndexDao
import com.colortablenotes.data.local.entities.ChecklistItem
import com.colortablenotes.data.local.entities.Note
import com.colortablenotes.data.local.entities.SearchIndex
import com.colortablenotes.data.local.entities.TableCell
import com.colortablenotes.data.local.entities.TextNote
import java.util.Date

@Database(
    entities = [
        Note::class,
        TextNote::class,
        ChecklistItem::class,
        TableCell::class,
        SearchIndex::class
    ],
    version = 1,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class NotesDatabase : RoomDatabase() {
    abstract fun noteDao(): NoteDao
    abstract fun searchIndexDao(): SearchIndexDao

    companion object {
        const val DATABASE_NAME = "notes_database"

        // Define migrations here if needed
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Migration logic
            }
        }
    }
}

class Converters {
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? = value?.let { Date(it) }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? = date?.time
}
