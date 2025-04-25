package com.qadis.lessonmaker

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.qadis.lessonmaker.Adapters.SubjectAdapter
import com.qadis.lessonmaker.Model.Subject
import com.qadis.lessonmaker.api.RetrofitClient
import com.qadis.lessonmaker.databinding.ActivityStudentDashboardBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class StudentDashboard : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var subjectAdapter: SubjectAdapter
    private val subjectsList = mutableListOf<Subject>()


    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val binding = ActivityStudentDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val userName = intent.getStringExtra("UserName") ?: "Unknown"
        binding.nameSem.text = "Welcome $userName"


        recyclerView = binding.studentDashboardListItem
        recyclerView.layoutManager = LinearLayoutManager(this)

        subjectAdapter = SubjectAdapter(
            subjectsList){
            subject->val intent= Intent(this@StudentDashboard, StudentNotes::class.java)
            startActivity(intent)
        }
        recyclerView.adapter = subjectAdapter

        fetchSubjects()


    }

    private fun fetchSubjects() {
        val receiveUserID = intent.getStringExtra("UserID") ?: ""
        val lastPart = receiveUserID.substringAfterLast("-")
        val finalID = "S$lastPart"
        Log.d("Received UserID:", finalID)
        val sessionID = 5

        RetrofitClient.instance.getEnrolledCourses(finalID, sessionID)
            .enqueue(object : Callback<List<Subject>> {
                @SuppressLint("NotifyDataSetChanged")
                override fun onResponse(
                    call: Call<List<Subject>>,
                    response: Response<List<Subject>>,
                ) {
                    if (response.isSuccessful) {
                        val subjects = response.body() ?: emptyList()
                        Log.d("API_RESPONSE", "Received Subjects List Size: ${subjects.size}")
                        for (subject in subjects) {
                            Log.d(
                                "API_SUBJECT",
                                "Subject Name: ${subject.subjectName}, Teacher Name: ${subject.teacherName}"
                            )
                        }

                        subjectsList.clear()
                        subjectsList.addAll(subjects)
                        subjectAdapter.notifyDataSetChanged()

                    } else {
                        val errorBody = response.errorBody()?.string()
                        Log.e(
                            "API_ERROR",
                            "Response failed - Code: ${response.code()}, Error: $errorBody"
                        )
                        Toast.makeText(
                            this@StudentDashboard,
                            "Failed to load subjects",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<List<Subject>>, t: Throwable) {
                    Log.e("API_ERROR", "Error fetching subjects", t)
                    Toast.makeText(
                        this@StudentDashboard,
                        "Error: ${t.localizedMessage}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }
}