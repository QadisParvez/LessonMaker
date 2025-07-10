package com.qadis.lessonmaker.model

data class DeletedContent(
    val id: Int = 0,
    val originalId: Int,
    val contentType: String, // "lesson", "note", "bookmark", "voice_note"
    val title: String,
    val content: String,
    val metadata: String, // JSON string with additional info
    val deletedAt: Long = System.currentTimeMillis(),
    val deletedBy: String, // user ID who deleted
    val canRestore: Boolean = true
)

data class RestoreRequest(
    val deletedContentId: Int,
    val restoreType: String
)

data class RestoreResponse(
    val isSuccess: Boolean,
    val message: String,
    val restoredContent: DeletedContent?
)