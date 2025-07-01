package com.qadis.lessonmaker.sqlite

data class DownloadedNote(
    val lessonId: Int,
    val subjectName: String,
    val weekNumber: Int,
    val htmlContent: String
)