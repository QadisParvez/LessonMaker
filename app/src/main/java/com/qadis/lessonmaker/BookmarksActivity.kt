package com.qadis.lessonmaker

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.qadis.lessonmaker.adapters.BookmarkAdapter
import com.qadis.lessonmaker.databinding.ActivityBookmarksBinding
import com.qadis.lessonmaker.model.Bookmark
import com.qadis.lessonmaker.sqlite.NotesDatabaseHelper

class BookmarksActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityBookmarksBinding
    private lateinit var bookmarkAdapter: BookmarkAdapter
    private lateinit var dbHelper: NotesDatabaseHelper
    private val bookmarksList = mutableListOf<Bookmark>()
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityBookmarksBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        dbHelper = NotesDatabaseHelper(this)
        
        // Setup RecyclerView
        bookmarkAdapter = BookmarkAdapter(bookmarksList) { bookmark ->
            // Open the lesson when bookmark is clicked
            val intent = Intent(this, ShowNotesActivity::class.java)
            intent.putExtra("lessonId", bookmark.lessonId)
            intent.putExtra("subjectName", bookmark.subjectName)
            startActivity(intent)
        }
        
        binding.bookmarksRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@BookmarksActivity)
            adapter = bookmarkAdapter
        }
        
        // Back button
        binding.backButton.setOnClickListener {
            finish()
        }
        
        // Load bookmarks
        loadBookmarks()
    }
    
    @SuppressLint("NotifyDataSetChanged")
    private fun loadBookmarks() {
        try {
            val bookmarks = dbHelper.getAllBookmarks()
            bookmarksList.clear()
            bookmarksList.addAll(bookmarks)
            bookmarkAdapter.notifyDataSetChanged()
            
            if (bookmarks.isEmpty()) {
                Toast.makeText(this, "No bookmarks found", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Error loading bookmarks: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
}