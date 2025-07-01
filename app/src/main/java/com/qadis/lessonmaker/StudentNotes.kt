package com.qadis.lessonmaker


import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.qadis.lessonmaker.adapters.StudentNotesAdapter
import com.qadis.lessonmaker.api.RetrofitClient
import com.qadis.lessonmaker.databinding.ActivityStudentNotesBinding
import com.qadis.lessonmaker.model.WeekNo
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class StudentNotes : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: StudentNotesAdapter
    private val weekList = mutableListOf<WeekNo>()
    private lateinit var binding: ActivityStudentNotesBinding
    private lateinit var courseCode: String


    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityStudentNotesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.backButton.setOnClickListener { finish() }

        val subjectName = intent.getStringExtra("SubjectName") ?: "Unknown Subject"
        courseCode = intent.getStringExtra("CourseCode") ?: ""
        Log.d("ReceivedCourseCode", "Received CourseCode: $courseCode")

        Log.d("StudentNotes", "Received CourseCode: $courseCode")
        binding.SubjectName.text = subjectName

        setupRecyclerView()
        setupLiveSearchListener()

        if (courseCode.isNotEmpty()) {
            loadWeeksFromApi(courseCode)
        } else {
            Toast.makeText(this, "No CourseCode provided!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupRecyclerView() {
        recyclerView = binding.recyclerViewNotes
        recyclerView.layoutManager =
            StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        adapter = StudentNotesAdapter(weekList) { selectedWeek ->
            val intent = Intent(this, ShowNotesActivity::class.java)
            intent.putExtra("lessonId", selectedWeek.id)
            startActivity(intent)
        }
        recyclerView.adapter = adapter
    }

    private fun setupLiveSearchListener() {
        binding.SearchByKeyword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val keyword = s.toString().trim()
                if (keyword.isNotEmpty()) {
                    searchNotesByKeyword(courseCode, keyword)
                } else {
                    loadWeeksFromApi(courseCode)
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun loadWeeksFromApi(courseCode: String) {
        RetrofitClient.instance.getWeeksByCC(courseCode)
            .enqueue(object : Callback<List<WeekNo>> {
                override fun onResponse(
                    call: Call<List<WeekNo>>,
                    response: Response<List<WeekNo>>
                ) {
                    if (response.isSuccessful) {
                        val newList = response.body() ?: emptyList()
                        adapter.updateList(newList)
                        Log.d("WeekNotes", "Fetched weeks: ${newList.size}")
                    } else {
                        Toast.makeText(this@StudentNotes, "No weeks found!", Toast.LENGTH_SHORT)
                            .show()
                    }
                }

                override fun onFailure(call: Call<List<WeekNo>>, t: Throwable) {
                    Toast.makeText(
                        this@StudentNotes,
                        "Error: ${t.localizedMessage}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }

    private fun searchNotesByKeyword(courseCode: String, keyword: String) {
        RetrofitClient.instance.getWeeksByCourseCodeAndKeyword(courseCode, keyword)
            .enqueue(object : Callback<List<WeekNo>> {
                override fun onResponse(
                    call: Call<List<WeekNo>>,
                    response: Response<List<WeekNo>>
                ) {
                    if (response.isSuccessful) {
                        val filteredList = response.body() ?: emptyList()
                        adapter.updateList(filteredList)
                        Log.d("SearchNotes", "Found ${filteredList.size} notes")
                    } else {
                        Toast.makeText(
                            this@StudentNotes,
                            "No matching notes found!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<List<WeekNo>>, t: Throwable) {
                    Toast.makeText(
                        this@StudentNotes,
                        "Search failed: ${t.localizedMessage}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }

}