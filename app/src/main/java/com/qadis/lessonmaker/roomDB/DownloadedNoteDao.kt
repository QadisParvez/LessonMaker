package com.qadis.lessonmaker.roomDB

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface DownloadedNoteDao {
    @Insert
    suspend fun insert(note: DownloadedNote)

    @Query("SELECT * FROM downloaded_notes ORDER BY weekNumber ASC")
    suspend fun getAllNotes(): List<DownloadedNote>
}