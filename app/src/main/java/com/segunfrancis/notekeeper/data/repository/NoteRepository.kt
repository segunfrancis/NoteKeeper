package com.segunfrancis.notekeeper.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.segunfrancis.notekeeper.data.local.NoteDao
import com.segunfrancis.notekeeper.data.local.NoteEntity
import dagger.hilt.android.scopes.ActivityRetainedScoped
import javax.inject.Inject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

@ActivityRetainedScoped
class NoteRepository @Inject constructor(
    private val dispatcher: CoroutineDispatcher,
    private val dao: NoteDao,
    private val firebaseAuth: FirebaseAuth
) : INoteRepository {
    override suspend fun addNote(note: NoteEntity) {
        withContext(dispatcher) {
            dao.addNote(note = note)
        }
    }

    override suspend fun getSingleNote(id: Long): NoteEntity {
        return withContext(dispatcher) {
            dao.getSingleNote(id = id)
        }
    }

    override fun getAllNotes(): Flow<List<NoteEntity>> {
        return dao.getAllNotes().flowOn(dispatcher)
    }

    override suspend fun updateNote(note: NoteEntity) {
        withContext(dispatcher) {
            dao.updateNote(note = note)
        }
    }

    override suspend fun deleteNote(note: NoteEntity) {
        withContext(dispatcher) {
            dao.deleteNote(note = note)
        }
    }

    override suspend fun deleteNoteById(id: Long) {
        withContext(dispatcher) {
            dao.deleteNoteById(id = id)
        }
    }

    override suspend fun loginUser(email: String, password: String) {
        firebaseAuth.signInWithEmailAndPassword(email, password).await()
    }

    override suspend fun createUser(email: String, password: String) {
        firebaseAuth.createUserWithEmailAndPassword(email, password).await()
    }
}
