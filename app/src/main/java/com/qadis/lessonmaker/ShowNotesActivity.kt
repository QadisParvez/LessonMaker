package com.qadis.lessonmaker

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.qadis.lessonmaker.roomDB.AppDatabase
import com.qadis.lessonmaker.roomDB.DownloadedNote
import com.qadis.lessonmaker.api.LessonContentResponse
import com.qadis.lessonmaker.api.RetrofitClient
import com.qadis.lessonmaker.databinding.ActivityShowNotesBinding
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class ShowNotesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityShowNotesBinding
    private var currentContent: String = ""
    private var currentWeekNumber: Int = 0
    private var currentLessonId: Int = -1


    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityShowNotesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        currentLessonId = intent.getIntExtra("lessonId", -1)
        if (currentLessonId == -1) {
            Toast.makeText(this, "Invalid lesson ID", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // WebView settings
        binding.WeekNotesContent.settings.javaScriptEnabled = true
        binding.WeekNotesContent.settings.domStorageEnabled = true

        // Fetch lesson content
        loadLessonContent(currentLessonId)

        // Back button
        binding.backButton.setOnClickListener {
            finish()
        }

        // Download button
        binding.DownloadButton.setOnClickListener {
            val dialog = AlertDialog.Builder(this@ShowNotesActivity)
                .setTitle("Download Confirmation")
                .setMessage("Do you want to download these notes?")
                .setPositiveButton("Yes") { dialog, _ ->
                    downloadNotesToRoom()
                    dialog.dismiss()
                }
                .setNegativeButton("No") { dialog, _ ->
                    dialog.dismiss()
                }
                .create()

            dialog.show()
            val greenColor = ContextCompat.getColor(this, R.color.biitGreen)
            dialog.getButton(AlertDialog.BUTTON_POSITIVE)?.setTextColor(greenColor)
            dialog.getButton(AlertDialog.BUTTON_NEGATIVE)?.setTextColor(greenColor)
        }
    }

    private fun loadLessonContent(lessonId: Int) {
        RetrofitClient.instance.getContentByLessonID(lessonId)
            .enqueue(object : Callback<LessonContentResponse> {
                override fun onResponse(
                    call: Call<LessonContentResponse>,
                    response: Response<LessonContentResponse>
                ) {
                    if (response.isSuccessful) {
                        currentContent = response.body()?.Content ?: "<p>No content found.</p>"
                        currentWeekNumber = response.body()?.WeekNumber ?: 0
                        binding.WeekNotesContent.loadDataWithBaseURL(
                            null,
                            currentContent,
                            "text/html",
                            "UTF-8",
                            null
                        )
                        Log.d("LessonHTML", currentContent)
                    } else {
                        Toast.makeText(this@ShowNotesActivity, "No notes found!", Toast.LENGTH_SHORT).show()
                        Log.e("NOTES_API", "Empty or invalid response")
                    }
                }

                override fun onFailure(call: Call<LessonContentResponse>, t: Throwable) {
                    Toast.makeText(this@ShowNotesActivity, "Failed: ${t.localizedMessage}", Toast.LENGTH_SHORT).show()
                    Log.e("NOTES_API_FAIL", "API Error", t)
                }
            })
    }

    private fun downloadNotesToRoom() {
        if (currentLessonId == -1 || currentContent.isBlank()) {
            Toast.makeText(this, "Nothing to download.", Toast.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch {
            try {
                val db = AppDatabase.getDatabase(applicationContext)
                val dao = db.noteDao()
                val note = DownloadedNote(
                    lessonId = currentLessonId,
                    weekNumber = currentWeekNumber,
                    content = currentContent
                )
                dao.insert(note)
                Toast.makeText(this@ShowNotesActivity, "Notes downloaded successfully", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Toast.makeText(this@ShowNotesActivity, "Failed to save notes.", Toast.LENGTH_SHORT).show()
                Log.e("ROOM_SAVE_ERROR", "${e.message}", e)
            }
        }
    }
}
