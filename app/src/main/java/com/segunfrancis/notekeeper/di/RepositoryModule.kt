package com.segunfrancis.notekeeper.di

import com.segunfrancis.notekeeper.data.repository.INoteRepository
import com.segunfrancis.notekeeper.data.repository.NoteRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent

@Module
@InstallIn(ActivityRetainedComponent::class)
abstract class RepositoryModule {

    @Binds
    abstract fun bindNoteRepository(repository: NoteRepository): INoteRepository
}
