package com.segunfrancis.notekeeper.data.repository

import com.segunfrancis.notekeeper.data.local.NoteEntity
import kotlinx.coroutines.flow.Flow

interface INoteRepository {
    suspend fun addNote(note: NoteEntity)

    suspend fun getSingleNote(id: Long): NoteEntity

    fun getAllNotes(): Flow<List<NoteEntity>>

    suspend fun updateNote(note: NoteEntity)

    suspend fun deleteNote(note: NoteEntity)

    suspend fun deleteNoteById(id: Long)

    suspend fun loginUser(email: String, password: String)

    suspend fun createUser(email: String, password: String)
}
