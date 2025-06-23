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
import com.qadis.lessonmaker.Adapters.StudentNotesAdapter
import com.qadis.lessonmaker.Model.WeekNo
import com.qadis.lessonmaker.api.RetrofitClient
import com.qadis.lessonmaker.databinding.ActivityStudentNotesBinding
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

        val subjectName = intent.getStringExtra("SubjectName") ?: "Unknown Subject"
        courseCode = intent.getStringExtra("CourseCode") ?: ""

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
            StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL).apply {
                gapStrategy = StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS
            }

        adapter = StudentNotesAdapter(weekList) { selectedWeek ->
            Toast.makeText(
                this,
                "Opening Notes For Week ${selectedWeek.weekNumber}",
                Toast.LENGTH_SHORT
            ).show()
            val intent = Intent(this, ShowNotesActivity::class.java)
            intent.putExtra("lessonId", selectedWeek.id)
            startActivity(intent)
        }

        recyclerView.adapter = adapter
    }

    private fun setupLiveSearchListener() {
        binding.SearchByKeyword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {
            }

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
                @SuppressLint("NotifyDataSetChanged")
                override fun onResponse(
                    call: Call<List<WeekNo>>,
                    response: Response<List<WeekNo>>
                ) {
                    if (response.isSuccessful) {
                        val fetchedWeeks = response.body() ?: emptyList()
                        weekList.clear()
                        weekList.addAll(fetchedWeeks)
                        adapter.notifyDataSetChanged()
                        Log.d("WeekNotes", "Fetched weeks: ${fetchedWeeks.size}")
                    } else {
                        Toast.makeText(this@StudentNotes, "No weeks found!", Toast.LENGTH_SHORT)
                            .show()
                    }
                }

                override fun onFailure(call: Call<List<WeekNo>>, t: Throwable) {
                    Log.e("WeekNotesAPI", "Failed to load weeks", t)
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
                @SuppressLint("NotifyDataSetChanged")
                override fun onResponse(
                    call: Call<List<WeekNo>>,
                    response: Response<List<WeekNo>>
                ) {
                    if (response.isSuccessful) {
                        weekList.clear()
                        weekList.addAll(response.body() ?: emptyList())
                        adapter.notifyDataSetChanged()
                        Log.d("SearchNotes", "Found ${weekList.size} notes")
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
                    Log.e("SearchError", "API call failed", t)
                }
            })
    }
}

