package com.qadis.lessonmaker

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.qadis.lessonmaker.api.RetrofitClient
import com.qadis.lessonmaker.databinding.ActivitySaveDialogBinding
import com.qadis.lessonmaker.model.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SaveDialog : AppCompatActivity() {

    private lateinit var binding: ActivitySaveDialogBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySaveDialogBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val htmlContent = intent.getStringExtra("htmlContent") ?: ""
        val keywordList = intent.getStringArrayListExtra("keywords") ?: arrayListOf()

        binding.SaveDialogBtn.setOnClickListener {
            val course = binding.edTextCrs.text.toString().trim()
            val week = binding.edTextWeekNo.text.toString().trim()

            val lessonId = course.toIntOrNull() ?: 0
            val weekNumber = week.toIntOrNull() ?: 1

            val tocList = extractTocFromHtml(htmlContent)

            val keywordsMapped = keywordList.map {
                KeywordData(lessonId = lessonId, keyword = it)
            }

            val page = PageData(
                lessonId = lessonId,
                pageNumber = weekNumber,
                content = htmlContent,
                tocList = tocList,
                keywords = keywordsMapped
            )


            val request = LessonRequest(pages = listOf(page))

            RetrofitClient.instance.createLesson(request)
                .enqueue(object : Callback<CreateLessonResponse> {
                    override fun onResponse(call: Call<CreateLessonResponse>, response: Response<CreateLessonResponse>) {
                        if (response.isSuccessful) {
                            Toast.makeText(this@SaveDialog, "Lesson saved successfully", Toast.LENGTH_SHORT).show()
                            finish()
                        } else {
                            Toast.makeText(this@SaveDialog, "Failed to save lesson", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<CreateLessonResponse>, t: Throwable) {
                        Toast.makeText(this@SaveDialog, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                    }
                })
        }
    }


    private fun extractTocFromHtml(html: String): List<TOCData> {
        val tocItems = mutableListOf<TOCData>()
        val regex = Regex("<h[1-3][^>]*>(.*?)</h[1-3]>")
        regex.findAll(html).forEach {
            tocItems.add(TOCData(title = it.groupValues[1], link = "#"))
        }
        return tocItems
    }
}
