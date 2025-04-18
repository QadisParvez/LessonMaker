package com.qadis.lessonmaker

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.qadis.lessonmaker.Adapters.TeacherAdapter
import com.qadis.lessonmaker.Adapters.TeacherAdapterRecent
import com.qadis.lessonmaker.Model.CurrentCourses
import com.qadis.lessonmaker.Model.Teacher
import com.qadis.lessonmaker.api.RetrofitClient
import com.qadis.lessonmaker.databinding.ActivityTeacherDashboardBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TeacherDashboard : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val teacherDashboard = ActivityTeacherDashboardBinding.inflate(layoutInflater)
        setContentView(teacherDashboard.root)

        val recyclerView1: RecyclerView = findViewById<RecyclerView>(R.id.CurrentCourses)
        val recyclerView2: RecyclerView = findViewById<RecyclerView>(R.id.RecentCourses)
        recyclerView1.layoutManager =
            LinearLayoutManager(this@TeacherDashboard, LinearLayoutManager.HORIZONTAL, false)
        recyclerView2.layoutManager =
            LinearLayoutManager(this@TeacherDashboard, LinearLayoutManager.VERTICAL, false)


        val currentCoursesList = mutableListOf<Teacher>()
        val recentCoursesList = mutableListOf<Teacher>()

        val adapter1 = TeacherAdapter(currentCoursesList) {}
        val adapter2 = TeacherAdapterRecent(recentCoursesList) {}

        recyclerView1.adapter = adapter1
        recyclerView2.adapter = adapter2

        fetchAllCourses(adapter2, recentCoursesList)
        getCurrentCourses(adapter1, currentCoursesList)



        teacherDashboard.openEditor.setOnClickListener {
            val openEditor = Intent(this@TeacherDashboard, EditorScreen::class.java)
            startActivity(openEditor)
        }


    }

    fun fetchAllCourses(
        adapter: TeacherAdapterRecent,
        recentCoursesList: MutableList<Teacher>,
    ) {
        RetrofitClient.instance.getAllCourses().enqueue(object : Callback<List<String>> {
            @SuppressLint("NotifyDataSetChanged")
            override fun onResponse(call: Call<List<String>>, response: Response<List<String>>) {
                if (response.isSuccessful) {
                    val courses = response.body() ?: emptyList()
                    Log.d("API_RESPONSE", "Courses: $courses")

                    recentCoursesList.clear()
                    for (course in courses) {
                        recentCoursesList.add(Teacher(course))
                    }
                    adapter.notifyDataSetChanged()
                } else {
                    Log.e("API_ERROR", "Response unsuccessful")
                }
            }

            override fun onFailure(call: Call<List<String>>, t: Throwable) {
                Log.e("API_ERROR", "Failed to fetch courses", t)
            }
        })
    }

    fun getCurrentCourses(adapter: TeacherAdapter, currentCourseList: MutableList<Teacher>) {
        val call = RetrofitClient.instance.getCurrentCourses("T124")

        Log.d("API_DEBUG", "Calling URL: ${call.request()}")

        call.enqueue(object : Callback<List<CurrentCourses>> {
            override fun onResponse(
                call: Call<List<CurrentCourses>>,
                response: Response<List<CurrentCourses>>
            ) {
                if (response.isSuccessful) {
                    val courses = response.body() ?: emptyList()
                    Log.d("API_RESPONSE", "Current Courses: $courses")

                    currentCourseList.clear()
                    for (course in courses) {
                        currentCourseList.add(Teacher(course.subjectName))
                    }
                    adapter.notifyDataSetChanged()
                } else {
                    Log.e("API_ERROR", "RESPONSE UNSUCCESSFUL - Code: ${response.code()}")
                    Log.e("API_ERROR", "Error Body: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<List<CurrentCourses>>, t: Throwable) {
                Log.e("API_ERROR", "Failed to fetch current courses", t)
            }
        })
    }
}
