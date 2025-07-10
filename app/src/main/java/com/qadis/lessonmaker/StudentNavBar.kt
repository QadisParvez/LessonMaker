package com.qadis.lessonmaker

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.qadis.lessonmaker.databinding.ActivityStudentNavBarBinding

class StudentNavBar : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val binding = ActivityStudentNavBarBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.DownloadedNotesButton.setOnClickListener {
            val intent = Intent(this@StudentNavBar, DownloadedNotes::class.java)
            startActivity(intent)
        }
        
        // Add new navigation buttons
        binding.BookmarksButton.setOnClickListener {
            val intent = Intent(this@StudentNavBar, BookmarksActivity::class.java)
            startActivity(intent)
        }
        
        binding.RecoveryButton.setOnClickListener {
            val intent = Intent(this@StudentNavBar, RecoveryActivity::class.java)
            startActivity(intent)
        }
        
        binding.Requests.setOnClickListener {
            // Existing functionality
        }
    }
}