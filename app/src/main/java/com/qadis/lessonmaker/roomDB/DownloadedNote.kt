package com.qadis.lessonmaker.roomDB

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "downloaded_notes")
data class DownloadedNote(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val lessonId: Int,
    val weekNumber: Int,
    val content: String
)
