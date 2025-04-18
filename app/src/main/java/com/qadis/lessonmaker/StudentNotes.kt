package com.qadis.lessonmaker

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityStudentNotesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        recyclerView = binding.recyclerViewNotes
        recyclerView.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL).apply {
            gapStrategy = StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS
        }

        adapter = StudentNotesAdapter(weekList) { selectedWeek ->
            Toast.makeText(this, "Opening Notes For ${selectedWeek.weekNumber}", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, ShowNotesActivity::class.java)
            intent.putExtra("week_no", selectedWeek.weekNumber)
            startActivity(intent)
        }

        recyclerView.adapter = adapter

        loadWeeksFromApi("CSC-101")
    }

    private fun loadWeeksFromApi(courseCode: String) {
        RetrofitClient.instance.getWeeksByCC(courseCode).enqueue(object : Callback<List<WeekNo>> {
            @SuppressLint("NotifyDataSetChanged")
            override fun onResponse(call: Call<List<WeekNo>>, response: Response<List<WeekNo>>) {
                if (response.isSuccessful) {
                    val fetchedWeeks = response.body() ?: emptyList()
                    weekList.clear()
                    weekList.addAll(fetchedWeeks)
                    adapter.notifyDataSetChanged()

                    Log.d("API_WeekNotes", "Fetched ${fetchedWeeks.size} weeks.")
                } else {
                    Toast.makeText(this@StudentNotes, "No data received!", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<WeekNo>>, t: Throwable) {
                Log.e("API_FAILURE_WeekNotes", "Failed to load weeks", t)
                Toast.makeText(this@StudentNotes, "Failed: ${t.localizedMessage}", Toast.LENGTH_LONG).show()
            }
        })
    }
}