package com.segunfrancis.notekeeper.work_manager

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.segunfrancis.notekeeper.data.local.NoteDao
import com.segunfrancis.notekeeper.data.local.NoteEntity
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.tasks.await
import timber.log.Timber

@HiltWorker
class NotesWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val dao: NoteDao,
    private val dispatcher: CoroutineDispatcher,
    private val firebaseAuth: FirebaseAuth,
    private val db: FirebaseFirestore
) : CoroutineWorker(appContext, workerParams) {

    data class Notes(val notes: List<NoteEntity>)
    override suspend fun doWork(): Result {
        return try {
            val notes = Notes(dao.getAllNotes().flowOn(dispatcher).first())
            db.collection(firebaseAuth.uid ?: "").document("data").set(notes).await()
            Result.success()
        } catch (e: Exception) {
            Timber.e(e)
            Result.failure()
        }
    }
}
