package com.colortablenotes.data.local.database

import androidx.room.*
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.colortablenotes.data.local.dao.NoteDao
import com.colortablenotes.data.local.dao.SearchIndexDao
import com.colortablenotes.data.local.entities.*
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
        const val MAX_NOTES_LIMIT = 5000

        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Future migration logic
            }
        }
    }
}

class Converters {
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }
}
