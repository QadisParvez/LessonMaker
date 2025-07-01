package com.qadis.lessonmaker.roomDB

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notes_table")
data class NotesEntity(
    @PrimaryKey val lessonId: String,
    val weekNumber: String,
    val content: String
)
