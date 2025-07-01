package com.qadis.lessonmaker

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.qadis.lessonmaker.adapters.DownloadedNotesAdapter
import com.qadis.lessonmaker.sqlite.NotesDatabaseHelper
import com.qadis.lessonmaker.databinding.ActivityDownloadedNotesBinding
import com.qadis.lessonmaker.sqlite.DownloadedNote

class DownloadedNotes : AppCompatActivity() {

    private lateinit var binding: ActivityDownloadedNotesBinding
    private lateinit var adapter: DownloadedNotesAdapter
    private lateinit var dbHelper: NotesDatabaseHelper
    private val noteList = mutableListOf<DownloadedNote>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityDownloadedNotesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dbHelper = NotesDatabaseHelper(this)

        binding.NoteWebView.settings.javaScriptEnabled = true
        binding.NoteWebView.settings.domStorageEnabled = true

        adapter = DownloadedNotesAdapter(
            notes = noteList,
            onViewClick = { note ->
                binding.SelectedSubjectName.text = note.subjectName
                binding.SelectedWeekNo.text = "Week ${note.weekNumber}"

                binding.SelectedSubjectName.visibility = View.VISIBLE
                binding.SelectedWeekNo.visibility = View.VISIBLE
                binding.NoteWebView.visibility = View.VISIBLE

                binding.NoteWebView.loadDataWithBaseURL(
                    null,
                    note.htmlContent,
                    "text/html",
                    "UTF-8",
                    null
                )
            },
            onShareClick = { note ->
                Toast.makeText(this, "Share lesson ${note.lessonId}", Toast.LENGTH_SHORT).show()
            }
        )

        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter

        loadDownloadedNotesFromDB()
    }

    private fun loadDownloadedNotesFromDB() {
        noteList.clear()
        noteList.addAll(dbHelper.getAllDownloadedNotes())
        adapter.notifyDataSetChanged()
    }
}
