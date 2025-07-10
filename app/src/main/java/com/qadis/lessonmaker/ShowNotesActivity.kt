        package com.qadis.lessonmaker

        import android.annotation.SuppressLint
        import android.content.Intent
        import android.os.Bundle
        import android.util.Log
        import android.view.Menu
        import android.view.MenuItem
        import android.widget.Toast
        import androidx.activity.enableEdgeToEdge
        import androidx.appcompat.app.AppCompatActivity
        import com.qadis.lessonmaker.api.LessonContentResponse
        import com.qadis.lessonmaker.api.RetrofitClient
        import com.qadis.lessonmaker.databinding.ActivityShowNotesBinding
        import com.qadis.lessonmaker.model.Bookmark
        import com.qadis.lessonmaker.sqlite.DownloadedNote
        import com.qadis.lessonmaker.sqlite.NotesDatabaseHelper
        import retrofit2.Call
        import retrofit2.Callback
        import retrofit2.Response
        import java.io.File
        class ShowNotesActivity : AppCompatActivity() {

            private lateinit var binding: ActivityShowNotesBinding
            private var currentContent: String = ""
            private var currentWeekNumber: Int = 0
            private var currentLessonId: Int = -1
            private var currentSubjectName: String = ""
            private var currentTeacherName: String = ""
            private var currentCourseCode: String = ""
            private lateinit var dbHelper: NotesDatabaseHelper

            @SuppressLint("SetJavaScriptEnabled")
            override fun onCreate(savedInstanceState: Bundle?) {
                super.onCreate(savedInstanceState)
                enableEdgeToEdge()
                binding = ActivityShowNotesBinding.inflate(layoutInflater)
                setContentView(binding.root)

                dbHelper = NotesDatabaseHelper(this)

                // Get data from intent
                currentLessonId = intent.getIntExtra("lessonId", -1)
                currentSubjectName = intent.getStringExtra("subjectName") ?: ""
                currentTeacherName = intent.getStringExtra("teacherName") ?: ""
                currentCourseCode = intent.getStringExtra("courseCode") ?: ""
                
                if (currentLessonId == -1) {
                    Toast.makeText(this, "Invalid lesson ID", Toast.LENGTH_SHORT).show()
                    finish()
                    return
                }

                // Enable JavaScript in WebView
                binding.WeekNotesContent.settings.javaScriptEnabled = true
                binding.WeekNotesContent.settings.domStorageEnabled = true

                // Load offline if exists, otherwise load from API
                val offlineContent = loadNoteFromInternalStorage(currentLessonId)
                if (offlineContent != null) {
                    displayContentWithTOC(offlineContent)
                    Toast.makeText(this, "Loaded offline note", Toast.LENGTH_SHORT).show()
                } else {
                    loadLessonContentFromAPI(currentLessonId)
                }

                // Back Button
                binding.backButton.setOnClickListener {
                    finish()
                }

                // Download/save note
                binding.DownloadButton.setOnClickListener {
                    val note = DownloadedNote(
                        lessonId = currentLessonId,
                        subjectName = currentSubjectName,
                        weekNumber = currentWeekNumber,
                        htmlContent = currentContent
                    )

                    val success = dbHelper.insertNote(note)

                    if (success) {
                        Toast.makeText(this, "Note saved to SQLite!", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, "Failed to save note.", Toast.LENGTH_SHORT).show()
                    }
                }

                // New buttons for enhanced features
                binding.bookmarkButton.setOnClickListener {
                    toggleBookmark()
                }

                binding.voiceNoteButton.setOnClickListener {
                    openVoiceNotes()
                }

                // Update bookmark button state
                updateBookmarkButton()
            }

            private fun toggleBookmark() {
                val isBookmarked = dbHelper.isBookmarked(currentLessonId)
                
                if (isBookmarked) {
                    // Remove bookmark
                    val success = dbHelper.removeBookmark(currentLessonId)
                    if (success) {
                        Toast.makeText(this, "Bookmark removed", Toast.LENGTH_SHORT).show()
                        updateBookmarkButton()
                    } else {
                        Toast.makeText(this, "Failed to remove bookmark", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    // Add bookmark
                    val bookmark = Bookmark(
                        lessonId = currentLessonId,
                        subjectName = currentSubjectName,
                        teacherName = currentTeacherName,
                        weekNumber = currentWeekNumber,
                        title = "Week $currentWeekNumber - $currentSubjectName",
                        courseCode = currentCourseCode
                    )
                    
                    val success = dbHelper.insertBookmark(bookmark)
                    if (success) {
                        Toast.makeText(this, "Bookmark added", Toast.LENGTH_SHORT).show()
                        updateBookmarkButton()
                    } else {
                        Toast.makeText(this, "Failed to add bookmark", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            private fun updateBookmarkButton() {
                val isBookmarked = dbHelper.isBookmarked(currentLessonId)
                binding.bookmarkButton.text = if (isBookmarked) "Remove Bookmark" else "Add Bookmark"
            }

            private fun openVoiceNotes() {
                val intent = Intent(this, VoiceNotesActivity::class.java)
                intent.putExtra("lessonId", currentLessonId)
                intent.putExtra("subjectName", currentSubjectName)
                startActivity(intent)
            }

            override fun onCreateOptionsMenu(menu: Menu?): Boolean {
                menuInflater.inflate(R.menu.show_notes_menu, menu)
                return true
            }

            override fun onOptionsItemSelected(item: MenuItem): Boolean {
                return when (item.itemId) {
                    R.id.action_bookmarks -> {
                        startActivity(Intent(this, BookmarksActivity::class.java))
                        true
                    }
                    R.id.action_voice_notes -> {
                        openVoiceNotes()
                        true
                    }
                    R.id.action_recovery -> {
                        startActivity(Intent(this, RecoveryActivity::class.java))
                        true
                    }
                    else -> super.onOptionsItemSelected(item)
                }
            }

            private fun loadLessonContentFromAPI(lessonId: Int) {
                RetrofitClient.instance.getContentByLessonID(lessonId)
                    .enqueue(object : Callback<LessonContentResponse> {
                        override fun onResponse(
                            call: Call<LessonContentResponse>,
                            response: Response<LessonContentResponse>
                        ) {
                            if (response.isSuccessful && response.body() != null) {
                                currentContent = response.body()!!.Content
                                currentWeekNumber = response.body()!!.WeekNumber
                                Log.d("LessonContent", "Week $currentWeekNumber: ${currentContent.take(100)}...")
                                displayContentWithTOC(currentContent)
                            } else {
                                Toast.makeText(this@ShowNotesActivity, "No notes found!", Toast.LENGTH_SHORT).show()
                            }
                        }

                        override fun onFailure(call: Call<LessonContentResponse>, t: Throwable) {
                            Toast.makeText(this@ShowNotesActivity, "Failed: ${t.localizedMessage}", Toast.LENGTH_SHORT).show()
                        }
                    })
            }

            private fun displayContentWithTOC(content: String) {
                // Prevent TOC duplication: Check if Table of Contents already exists
                if (content.contains("<ul class=\"toc-list\">")) {
                    binding.WeekNotesContent.loadDataWithBaseURL(null, content, "text/html", "UTF-8", null)
                    currentContent = content
                    return
                }

                val headingRegex = Regex("<h([1-3])[^>]*>(.*?)</h[1-3]>")
                val headings = headingRegex.findAll(content)

                if (headings.none()) {
                    binding.WeekNotesContent.loadDataWithBaseURL(null, content, "text/html", "UTF-8", null)
                    currentContent = content
                    return
                }

                val tocBuilder = StringBuilder(
                    """
                <style>
                    ul.toc-list { list-style-type: none; padding-left: 0; }
                    ul.toc-list li { margin: 5px 0; }
                    ul.toc-list li a { color: black; text-decoration: none; font-weight: bold; }
                    ul.toc-list li a:hover { text-decoration: underline; }
                </style>
                <h2 style="color:black;"><u>Table of Contents</u></h2>
                <ul class="toc-list">
            """.trimIndent()
                )

                var modifiedContent = content
                var index = 0

                headings.forEach { match ->
                    val level = match.groupValues[1]
                    val title = match.groupValues[2].replace(Regex("<[^>]*>"), "")
                    val anchor = "section_$index"

                    tocBuilder.append("<li><a href='#$anchor'>$title</a></li>")

                    val originalTag = match.value
                    val headingWithAnchor = originalTag.replace(
                        Regex("<h$level"),
                        "<h$level id='$anchor'"
                    )
                    modifiedContent = modifiedContent.replace(originalTag, headingWithAnchor)

                    index++
                }

                tocBuilder.append("</ul><hr>")

                val finalHtml = tocBuilder.toString() + modifiedContent

                binding.WeekNotesContent.loadDataWithBaseURL(null, finalHtml, "text/html", "UTF-8", null)
                currentContent = finalHtml
            }



            private fun saveNoteToSQLite(lessonId: Int, subjectName: String, weekNo: Int, htmlContent: String): Boolean {
                val dbHelper = NotesDatabaseHelper(this)

                if (dbHelper.isNoteExists(lessonId)) {
                    Toast.makeText(this, "Note already saved!", Toast.LENGTH_SHORT).show()
                    return false
                }

                val note = DownloadedNote(
                    lessonId = lessonId,
                    subjectName = subjectName,
                    weekNumber = weekNo,
                    htmlContent = htmlContent
                )

                return dbHelper.insertNote(note)
            }


            private fun loadNoteFromInternalStorage(lessonId: Int): String? {
                val file = File(filesDir, "lesson_$lessonId.html")
                return if (file.exists()) file.readText() else null
            }
        }