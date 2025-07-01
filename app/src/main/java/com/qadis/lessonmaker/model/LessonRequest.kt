package com.qadis.lessonmaker.model

data class LessonRequest(
    val pages: List<PageData>
)

data class PageData(
    val lessonId: Int,
    val pageNumber: Int,
    val content: String,
    val tocList: List<TOCData>,
    val keywords: List<KeywordData>
)

data class TOCData(
    val title: String,
    val link: String
)

data class KeywordData(
    val lessonId: Int,
    val keyword: String
)

data class CreateLessonResponse(
    val isSuccess: Boolean,
    val message: String,
    val savedKeywords: List<KeywordData>
)

