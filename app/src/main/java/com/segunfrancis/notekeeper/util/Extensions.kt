package com.segunfrancis.notekeeper.util

import android.util.Patterns
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.segunfrancis.notekeeper.data.local.NoteEntity
import com.segunfrancis.notekeeper.ui.model.NoteItem
import java.util.regex.Pattern

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
