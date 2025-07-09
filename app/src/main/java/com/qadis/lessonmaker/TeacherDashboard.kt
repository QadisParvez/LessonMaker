package com.qadis.lessonmaker

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.qadis.lessonmaker.adapters.TeacherAdapter
import com.qadis.lessonmaker.adapters.TeacherAdapterRecent
import com.qadis.lessonmaker.api.RetrofitClient
import com.qadis.lessonmaker.databinding.ActivityTeacherDashboardBinding
import com.qadis.lessonmaker.model.CurrentCourses
import com.qadis.lessonmaker.model.Teacher
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class TeacherDashboard : AppCompatActivity() {

    private lateinit var teacherId: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val teacherDashboard = ActivityTeacherDashboardBinding.inflate(layoutInflater)
        setContentView(teacherDashboard.root)

        // Get teacher ID from intent
        teacherId = intent.getStringExtra("UserID") ?: ""

        val recyclerView1: RecyclerView = findViewById(R.id.CurrentCourses)
        val recyclerView2: RecyclerView = findViewById(R.id.RecentCourses)

        recyclerView1.layoutManager = LinearLayoutManager(this@TeacherDashboard, LinearLayoutManager.HORIZONTAL, false)
        recyclerView2.layoutManager = LinearLayoutManager(this@TeacherDashboard, LinearLayoutManager.VERTICAL, false)

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

        teacherDashboard.menuButton.setOnClickListener {
            val intent = Intent(this@TeacherDashboard, TeacheNav::class.java)
            startActivity(intent)
        }

        teacherDashboard.Logout.setOnClickListener {
            val sharedPref = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
            sharedPref.edit().clear().apply()
            startActivity(Intent(this, LoginPage::class.java))
            finish()
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
        RetrofitClient.instance.getTeacherCourseDetails(teacherId)
            .enqueue(object : Callback<List<String>> {
                @SuppressLint("NotifyDataSetChanged")
                override fun onResponse(
                    call: Call<List<String>>,
                    response: Response<List<String>>
                ) {
                    if (response.isSuccessful) {
                        val courses = response.body() ?: emptyList()
                        Log.d("API_RESPONSE", "Teacher's Courses: $courses")

                        currentCourseList.clear()
                        for (title in courses) {
                            currentCourseList.add(Teacher(title)) // Wrap string into Teacher model
                        }
                        adapter.notifyDataSetChanged()
                    } else {
                        Log.e("API_ERROR", "RESPONSE UNSUCCESSFUL - Code: ${response.code()}")
                        Log.e("API_ERROR", "Error Body: ${response.errorBody()?.string()}")
                    }
                }

                override fun onFailure(call: Call<List<String>>, t: Throwable) {
                    Log.e("API_ERROR", "Failed to fetch teacher courses", t)
                }
            })
    }


}