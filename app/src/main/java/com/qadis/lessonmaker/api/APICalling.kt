package com.qadis.lessonmaker.api

import com.qadis.lessonmaker.model.*
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

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
    fun getAllCourses(): Call<List<String>>

    @GET("Courses/getTeacherCourseDetails")
    fun getTeacherCourseDetails(@Query("teacherID") teacherId: String): Call<List<String>>

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

    // New endpoints for enhanced features
    @GET("Bookmarks/GetBookmarks")
    fun getBookmarks(@Query("userId") userId: String): Call<BookmarkResponse>

    @POST("Bookmarks/AddBookmark")
    fun addBookmark(@Body request: BookmarkRequest): Call<BookmarkResponse>

    @POST("Bookmarks/RemoveBookmark")
    fun removeBookmark(@Body request: BookmarkRequest): Call<BookmarkResponse>

    @GET("VoiceNotes/GetVoiceNotes")
    fun getVoiceNotes(@Query("lessonId") lessonId: Int): Call<VoiceNoteResponse>

    @POST("VoiceNotes/SaveVoiceNote")
    fun saveVoiceNote(@Body request: VoiceNoteRequest): Call<VoiceNoteResponse>

    @GET("Recovery/GetDeletedContent")
    fun getDeletedContent(@Query("userId") userId: String): Call<List<DeletedContent>>

    @POST("Recovery/RestoreContent")
    fun restoreContent(@Body request: RestoreRequest): Call<RestoreResponse>

    @GET("Config/GetAppConfig")
    fun getAppConfig(): Call<AppConfig>

    @POST("Config/UpdateAppConfig")
    fun updateAppConfig(@Body config: AppConfig): Call<AppConfig>
}
