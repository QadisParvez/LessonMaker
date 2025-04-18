package com.qadis.lessonmaker.Model

import com.google.gson.annotations.SerializedName

data class Subject(
    @SerializedName("title") val subjectName: String?,
    @SerializedName("name") val teacherName: String?
)

