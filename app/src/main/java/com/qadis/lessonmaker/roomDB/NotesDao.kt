package com.qadis.lessonmaker.roomDB

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface NotesDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: NotesEntity)

    @Query("SELECT * FROM notes_table")
    suspend fun getAllNotes(): List<NotesEntity>

    @Query("SELECT * FROM notes_table WHERE lessonId = :lessonId")
    suspend fun getNoteById(lessonId: String): NotesEntity?

    @Delete
    suspend fun deleteNote(note: NotesEntity)
}
