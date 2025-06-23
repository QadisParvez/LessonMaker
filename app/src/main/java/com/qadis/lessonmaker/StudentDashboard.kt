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

        // Side Navigation
        binding.SideNavigation.setOnClickListener {
            val intent = Intent(this@StudentDashboard, StudentNavBar::class.java)
            startActivity(intent)
        }

        // Logout
        binding.Logout.setOnClickListener {
            val sharedPref = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
            sharedPref.edit().clear().apply()
            startActivity(Intent(this, LoginPage::class.java))
            finish()
        }

        // Show username
        val userName = intent.getStringExtra("UserName") ?: "Unknown"
        binding.nameSem.text = "Welcome $userName"

        // RecyclerView setup
        recyclerView = binding.studentDashboardListItem
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Adapter setup
        subjectAdapter = SubjectAdapter(subjectsList) { subject ->
            val intent = Intent(this@StudentDashboard, StudentNotes::class.java)
            intent.putExtra("SubjectName", subject.subjectName)
            intent.putExtra("CourseCode", subject.courseCode)
            startActivity(intent)
        }

        recyclerView.adapter = subjectAdapter

        // Start fetching subjects
        fetchSubjects()
    }

    private fun fetchSubjects() {
        val receiveUserID = intent.getStringExtra("UserID")

        if (receiveUserID.isNullOrEmpty()) {
            Toast.makeText(this, "Error: UserID not found in intent", Toast.LENGTH_SHORT).show()
            Log.e("IntentError", "UserID missing from intent")
            return
        }

        val lastPart = receiveUserID.substringAfterLast("-")
        val finalID = "S$lastPart"
        val sessionID = 5

        Log.d("ReceivedUserID", "Raw: $receiveUserID → FinalID: $finalID")

        RetrofitClient.instance.getEnrolledCourses(finalID, sessionID)
            .enqueue(object : Callback<List<Subject>> {
                @SuppressLint("NotifyDataSetChanged")
                override fun onResponse(call: Call<List<Subject>>, response: Response<List<Subject>>) {
                    if (response.isSuccessful) {
                        val subjects = response.body() ?: emptyList()

                        Log.d("API_RESPONSE", "Subjects List Size: ${subjects.size}")
                        Log.d("API_RAW", subjects.toString())

                        if (subjects.isEmpty()) {
                            Toast.makeText(this@StudentDashboard, "No subjects found!", Toast.LENGTH_SHORT).show()
                        }

                        subjectsList.clear()
                        subjectsList.addAll(subjects)
                        subjectAdapter.notifyDataSetChanged()
                    } else {
                        val errorBody = response.errorBody()?.string()
                        Log.e("API_ERROR", "Code: ${response.code()}, Error: $errorBody")
                        Toast.makeText(this@StudentDashboard, "Failed to load subjects", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<List<Subject>>, t: Throwable) {
                    Log.e("API_FAILURE", "Error fetching subjects", t)
                    Toast.makeText(this@StudentDashboard, "Error: ${t.localizedMessage}", Toast.LENGTH_SHORT).show()
                }
            })
    }
}