package com.segunfrancis.notekeeper.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addNote(note: NoteEntity)

    @Query("SELECT * FROM note_table WHERE note_id is :id")
    suspend fun getSingleNote(id: Long): NoteEntity

    @Query("SELECT * FROM note_table ORDER BY note_id DESC")
    fun getAllNotes(): Flow<List<NoteEntity>>

    @Update
    suspend fun updateNote(note: NoteEntity)

    @Delete
    suspend fun deleteNote(note: NoteEntity)

    @Query("DELETE FROM note_table WHERE note_id is :id")
    suspend fun deleteNoteById(id: Long)
}
