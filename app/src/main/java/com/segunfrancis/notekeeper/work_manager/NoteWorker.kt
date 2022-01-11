package com.segunfrancis.notekeeper.work_manager

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.segunfrancis.notekeeper.data.local.NoteDao
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.flowOn

@HiltWorker
class NoteWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val dao: NoteDao,
    private val dispatcher: CoroutineDispatcher
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        dao.getAllNotes().flowOn(dispatcher)
        return Result.success()
    }
}
