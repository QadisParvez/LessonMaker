package com.qadis.lessonmaker

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.qadis.lessonmaker.Adapters.DownloadedNotesAdapter
import com.qadis.lessonmaker.Model.Notes
import com.qadis.lessonmaker.databinding.ActivityDownloadedNotesBinding

class DownloadedNotes : AppCompatActivity() {

    private lateinit var binding: ActivityDownloadedNotesBinding
    private lateinit var notesAdapter: DownloadedNotesAdapter
    private lateinit var notesList: List<Notes>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityDownloadedNotesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.mainContent.settings.javaScriptEnabled = true

        notesList = listOf(
            Notes("Math", "Sir Ali", "<h2>Math Notes</h2><p>This is math content</p>"),
            Notes("Physics", "Miss Sana", "<h2>Physics Notes</h2><p>Some physics content here</p>"),
            Notes("Chemistry", "Sir Usman", "<h2>Chemistry</h2><p>Details of chemistry</p>")
        )

        notesAdapter = DownloadedNotesAdapter(notesList) { selectedNote ->
            binding.mainContent.loadDataWithBaseURL(
                null,
                selectedNote.content,
                "text/html",
                "UTF-8",
                null
            )
            binding.main.visibility = View.VISIBLE
        }

        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = notesAdapter



    }
}