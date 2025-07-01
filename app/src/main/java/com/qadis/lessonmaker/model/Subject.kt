package com.qadis.lessonmaker.model

import com.google.gson.annotations.SerializedName

data class Subject(
    @SerializedName("title")
    val subjectName: String,

    @SerializedName("name")
    val teacherName: String,

    @SerializedName("code")
    val courseCode: String
)