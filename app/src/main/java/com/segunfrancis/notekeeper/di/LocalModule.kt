package com.segunfrancis.notekeeper.di

import android.content.Context
import com.segunfrancis.notekeeper.data.local.NoteDao
import com.segunfrancis.notekeeper.data.local.NoteDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

@Module
@InstallIn(SingletonComponent::class)
class LocalModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext appContext: Context): NoteDatabase {
        return NoteDatabase.getNoteDatabase(context = appContext)
    }

    @Provides
    @Singleton
    fun provideDao(database: NoteDatabase): NoteDao {
        return database.dao()
    }

    @Provides
    @Singleton
    fun provideDispatcher(): CoroutineDispatcher {
        return Dispatchers.IO
    }
}
