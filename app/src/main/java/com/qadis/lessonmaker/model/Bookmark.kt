package com.qadis.lessonmaker.model

data class Bookmark(
    val id: Int = 0,
    val lessonId: Int,
    val subjectName: String,
    val teacherName: String,
    val weekNumber: Int,
    val title: String,
    val courseCode: String,
    val bookmarkedAt: Long = System.currentTimeMillis(),
    val isActive: Boolean = true
)

data class BookmarkRequest(
    val lessonId: Int,
    val action: String // "add" or "remove"
)

data class BookmarkResponse(
    val isSuccess: Boolean,
    val message: String,
    val bookmarks: List<Bookmark>
)