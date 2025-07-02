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
import com.qadis.lessonmaker.adapters.SubjectAdapter
import com.qadis.lessonmaker.model.Subject
import com.qadis.lessonmaker.api.RetrofitClient
import com.qadis.lessonmaker.databinding.ActivityStudentDashboardBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class StudentDashboard : AppCompatActivity() {

    private lateinit var binding: ActivityStudentDashboardBinding
    private lateinit var recyclerView: RecyclerView
    private lateinit var subjectAdapter: SubjectAdapter
    private val subjectsList = mutableListOf<Subject>()


    @SuppressLint("SetTextI18n", "UseKtx")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityStudentDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Logout
        binding.Logout.setOnClickListener {
            getSharedPreferences("MyAppPrefs", MODE_PRIVATE).edit().clear().apply()
            startActivity(Intent(this, LoginPage::class.java))
            finish()
        }

        // Side navigation
        binding.SideNavigation.setOnClickListener {
            startActivity(Intent(this, StudentNavBar::class.java))
        }

        // Greet user
        val userName = intent.getStringExtra("UserName") ?: "Unknown"
        binding.nameSem.text = "Welcome $userName"

        // RecyclerView + Adapter
        subjectAdapter = SubjectAdapter(subjectsList) { subject ->
            if (!subject.courseCode.isNullOrBlank()) {
                val intent = Intent(this, StudentNotes::class.java)
                intent.putExtra("lessonId", subject.lessonID)
                intent.putExtra("SubjectName", subject.subjectName)
                intent.putExtra("CourseCode", subject.courseCode)
                Log.d("CourseCode To be Passed", "Course Code sent: ${subject.courseCode}")
                startActivity(intent)
            } else {
                Toast.makeText(this, "No course code available", Toast.LENGTH_SHORT).show()
            }
        }

        binding.studentDashboardListItem.apply {
            layoutManager = LinearLayoutManager(this@StudentDashboard)
            adapter = subjectAdapter
        }

        fetchSubjects()
    }

    private fun fetchSubjects() {
        val receiveUserID = intent.getStringExtra("UserID")
        Log.d("DEBUG_INTENT", "Received UserID: $receiveUserID")

        if (receiveUserID.isNullOrEmpty()) {
            Toast.makeText(this, "Error: UserID not found in intent", Toast.LENGTH_SHORT).show()
            return
        }

        val finalID = receiveUserID // already in S4566 format
        val sessionID = 5

        RetrofitClient.instance.getEnrolledCourses(finalID, sessionID)
            .enqueue(object : Callback<List<Subject>> {
                @SuppressLint("NotifyDataSetChanged")
                override fun onResponse(call: Call<List<Subject>>, response: Response<List<Subject>>) {
                    if (response.isSuccessful) {
                        val subjects = response.body() ?: emptyList()

                        subjectsList.clear()

                        subjects.forEach { subject ->
                            if (!subject.subjectName.isNullOrBlank() && !subject.courseCode.isNullOrBlank()) {
                                subjectsList.add(subject)
                            } else {
                                Log.w("DEBUG_SUBJECTS", "Skipped invalid subject: $subject")
                            }
                        }

                        subjectAdapter.notifyDataSetChanged()

                        if (subjectsList.isEmpty()) {
                            Toast.makeText(this@StudentDashboard, "No enrolled subjects found", Toast.LENGTH_SHORT).show()
                        } else {
                            Log.d("DEBUG_SUBJECTS", "Subjects loaded: ${subjectsList.size}")
                        }

                    } else {
                        Toast.makeText(this@StudentDashboard, "Failed to load subjects", Toast.LENGTH_SHORT).show()
                        Log.e("API_ERROR", "Response Code: ${response.code()}")
                    }
                }

                override fun onFailure(call: Call<List<Subject>>, t: Throwable) {
                    Toast.makeText(this@StudentDashboard, "Error: ${t.localizedMessage}", Toast.LENGTH_SHORT).show()
                    Log.e("API_ERROR", "Network failure", t)
                }
            })
    }
}