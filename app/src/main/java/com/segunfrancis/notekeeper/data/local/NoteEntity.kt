package com.segunfrancis.notekeeper.data.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "note_table")
data class NoteEntity(
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "note_id") val id: Long = 0,
    @ColumnInfo(name = "note_title") val title: String,
    @ColumnInfo(name = "note_description") val description: String
)
