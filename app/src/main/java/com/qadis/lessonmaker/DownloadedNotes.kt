package com.qadis.lessonmaker


import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.webkit.WebSettings
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.qadis.lessonmaker.adapters.DownloadedNotesAdapter
import com.qadis.lessonmaker.databinding.ActivityDownloadedNotesBinding
import com.qadis.lessonmaker.sqlite.DownloadedNote
import com.qadis.lessonmaker.sqlite.NotesDatabaseHelper
import java.io.File


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
        dbHelper = NotesDatabaseHelper(this)

        setupWebView()
        setupRecyclerView()
        loadDownloadedNotesFromDB()
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun setupWebView() {
        val settings: WebSettings = binding.NoteWebView.settings
        settings.javaScriptEnabled = true
        settings.domStorageEnabled = true
        binding.NoteWebView.setBackgroundColor(0xFFFFFF)
    }

    @SuppressLint("SetTextI18n")
    private fun setupRecyclerView() {
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
                shareNoteContent(note)
            }
        )

        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun loadDownloadedNotesFromDB() {
        noteList.clear()
        noteList.addAll(dbHelper.getAllDownloadedNotes())
        adapter.notifyDataSetChanged()
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun shareNoteContent(note: DownloadedNote) {
        val pdfFile = File(getExternalFilesDir(null), "Lesson_${note.lessonId}.pdf")

        val webView = android.webkit.WebView(this)
        webView.settings.javaScriptEnabled = true
        webView.loadDataWithBaseURL(null, note.htmlContent, "text/html", "utf-8", null)

        webView.post {
            val webView = android.webkit.WebView(this)
            webView.settings.javaScriptEnabled = true

            webView.webViewClient = object : android.webkit.WebViewClient() {
                override fun onPageFinished(view: android.webkit.WebView?, url: String?) {
                    // WebView is fully loaded, now draw it
                    val document = android.graphics.pdf.PdfDocument()
                    val pageInfo =
                        android.graphics.pdf.PdfDocument.PageInfo.Builder(792, 1120, 1).create()
                    val page = document.startPage(pageInfo)
                    webView.draw(page.canvas)
                    document.finishPage(page)

                    try {
                        val pdfFile = File(getExternalFilesDir(null), "Lesson_${note.lessonId}.pdf")
                        pdfFile.outputStream().use {
                            document.writeTo(it)
                        }
                        document.close()

                        val uri = FileProvider.getUriForFile(
                            this@DownloadedNotes,
                            "$packageName.fileprovider",
                            pdfFile
                        )

                        val intent = Intent(Intent.ACTION_SEND)
                        intent.type = "application/pdf"
                        intent.putExtra(Intent.EXTRA_STREAM, uri)
                        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                        startActivity(Intent.createChooser(intent, "Share Note PDF"))

                    } catch (e: Exception) {
                        e.printStackTrace()
                        Toast.makeText(
                            this@DownloadedNotes,
                            "PDF creation failed",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }

// Important: this must be set *before* loadData
            webView.loadDataWithBaseURL(null, note.htmlContent, "text/html", "UTF-8", null)

        }
    }
}