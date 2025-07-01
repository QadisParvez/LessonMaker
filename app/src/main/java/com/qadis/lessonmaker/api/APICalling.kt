package com.qadis.lessonmaker.api

import com.qadis.lessonmaker.model.CreateLessonResponse
import com.qadis.lessonmaker.model.CurrentCourses
import com.qadis.lessonmaker.model.LessonRequest
import com.qadis.lessonmaker.model.Subject
import com.qadis.lessonmaker.model.WeekNo
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
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

    @GET("Enrollment/ViewEnrolledCourses")
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

    @POST("Pages/CreateLesson")
    fun createLesson(@Body request: LessonRequest): Call<CreateLessonResponse>







}
