package com.segunfrancis.notekeeper.util

import android.util.Patterns
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LiveData
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.segunfrancis.notekeeper.data.local.NoteEntity
import com.segunfrancis.notekeeper.ui.model.NoteItem
import com.segunfrancis.notekeeper.work_manager.NotesWorker
import java.util.concurrent.TimeUnit
import java.util.regex.Pattern
import timber.log.Timber

fun NoteEntity.mapToUi(): NoteItem {
    return NoteItem(
        id = id,
        title = title,
        description = description
    )
}

fun NoteItem.mapToData(): NoteEntity {
    return NoteEntity(
        id = id,
        title = title,
        description = description
    )
}

fun Fragment.displayDialog(
    @StringRes title: Int,
    @StringRes description: Int,
    callback: () -> Unit
) {
    MaterialAlertDialogBuilder(requireContext())
        .setTitle(title)
        .setMessage(description)
        .setPositiveButton("Yes") { dialog, _ ->
            callback.invoke()
            dialog.dismiss()
        }
        .setNegativeButton("No") { dialog, _ ->
            dialog.dismiss()
        }
        .create()
        .show()
}

fun String.isEmailValid(): Boolean {
    return Pattern.matches(Patterns.EMAIL_ADDRESS.pattern(), this)
}

fun String.isPasswordValid(): Boolean {
    return length > 5
}

fun FragmentActivity.initDataBackup() {
    val workManager: WorkManager = WorkManager.getInstance(this.applicationContext)
    val outputWorkInfoItems: LiveData<List<WorkInfo>> = workManager.getWorkInfosByTagLiveData(
        AppConstants.WORKER_TAG
    )
    val constraints = Constraints.Builder()
        .setRequiresBatteryNotLow(true)
        .setRequiredNetworkType(NetworkType.CONNECTED)
        .build()
    val getNoteRequest = PeriodicWorkRequestBuilder<NotesWorker>(1, TimeUnit.DAYS)
        .setConstraints(constraints)
        .addTag(AppConstants.WORKER_TAG)
        .build()
    workManager.enqueueUniquePeriodicWork(
        "UPLOAD_WORK",
        ExistingPeriodicWorkPolicy.KEEP,
        getNoteRequest
    )

    outputWorkInfoItems.observe(this) {
        Timber.d(it.toString())
    }
}
