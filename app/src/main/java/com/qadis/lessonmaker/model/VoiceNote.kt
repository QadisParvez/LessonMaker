package com.qadis.lessonmaker.model

data class VoiceNote(
    val id: Int = 0,
    val lessonId: Int,
    val subjectName: String,
    val audioFilePath: String,
    val duration: Long, // in milliseconds
    val title: String,
    val description: String = "",
    val recordedAt: Long = System.currentTimeMillis(),
    val isActive: Boolean = true
)

data class VoiceNoteRequest(
    val lessonId: Int,
    val title: String,
    val description: String,
    val duration: Long
)

data class VoiceNoteResponse(
    val isSuccess: Boolean,
    val message: String,
    val voiceNotes: List<VoiceNote>
)