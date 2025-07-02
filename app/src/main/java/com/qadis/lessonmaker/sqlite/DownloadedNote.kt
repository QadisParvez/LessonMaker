package com.qadis.lessonmaker.sqlite

data class DownloadedNote(
    val id: Int = 0,
    val lessonId: Int,
    val subjectName: String,
    val weekNumber: Int,
    val htmlContent: String
)
