package com.colortablenotes.di

import android.content.Context
import androidx.room.Room
import com.colortablenotes.data.local.dao.NoteDao
import com.colortablenotes.data.local.dao.SearchIndexDao
import com.colortablenotes.data.local.database.NotesDatabase
import com.colortablenotes.data.repository.NotesRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideNotesDatabase(
        @ApplicationContext context: Context
    ): NotesDatabase {
        return Room.databaseBuilder(
            context,
            NotesDatabase::class.java,
            NotesDatabase.DATABASE_NAME
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    fun provideNoteDao(database: NotesDatabase): NoteDao = database.noteDao()

    @Provides
    fun provideSearchIndexDao(database: NotesDatabase): SearchIndexDao = database.searchIndexDao()

    @Provides
    @Singleton
    fun provideNotesRepository(
        noteDao: NoteDao,
        searchIndexDao: SearchIndexDao,
        database: NotesDatabase
    ): NotesRepository = NotesRepository(noteDao, searchIndexDao, database)
}
