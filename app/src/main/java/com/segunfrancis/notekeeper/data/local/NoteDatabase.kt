package com.segunfrancis.notekeeper.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(version = 1, exportSchema = true, entities = [NoteEntity::class])
abstract class NoteDatabase : RoomDatabase() {

    abstract fun dao(): NoteDao

    companion object {
        fun getNoteDatabase(context: Context): NoteDatabase =
            Room.databaseBuilder(context, NoteDatabase::class.java, "note_database")
                .build()
    }
}
