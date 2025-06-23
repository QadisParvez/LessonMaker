package com.qadis.lessonmaker.api

import com.qadis.lessonmaker.Model.CurrentCourses
import com.qadis.lessonmaker.Model.Subject
import com.qadis.lessonmaker.Model.WeekNo
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

data class UserResponse(
    val success: Boolean,
    val role: Int,
    val name: String,
    val studentId: String?
)

data class LessonContentResponse(
    val Content: String,
    val WeekNumber: Int
)


interface ApiService {
    @GET("Users/GetUsers/")
    fun getUser(
        @Query("id") id: String,
        @Query("password") password: String
    ): Call<UserResponse>

    @GET("Enrollment/getEnrolledCourses")
    fun getEnrolledCourses(
        @Query("stdID") studentId: String,
        @Query("sessionID") sessionId: Int
    ): Call<List<Subject>>


    @GET("Courses/GetCourses")
    fun getAllCourses():Call<List<String>>

    @GET("Courses/GetCurrentCourses")
    fun getCurrentCourses(
        @Query("id") teacherID:String
    ):Call<List<CurrentCourses>>

    @GET("Lesson/SearchByCourseCode")
    fun getWeeksByCC(
        @Query("courseCode") courseCode: String
    ): Call<List<WeekNo>>


    @GET("Pages/GetContentByLessonID")
    fun getContentByLessonID(
        @Query("LessonID") lessonID: Int
    ): Call<LessonContentResponse>

    @GET("Pages/GetWeeksByCourseCodeAndKeyword")
    fun getWeeksByCourseCodeAndKeyword(
        @Query("courseCode") courseCode: String,
        @Query("keyword") keyword: String
    ): Call<List<WeekNo>>







}
