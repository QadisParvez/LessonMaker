package com.qadis.lessonmaker

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.qadis.lessonmaker.databinding.ActivityShowNotesBinding


class ShowNotesActivity : AppCompatActivity() {
    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val showStudentNotes= ActivityShowNotesBinding.inflate(layoutInflater)
        setContentView(showStudentNotes.root)

        val programmingContent = """
    <html>
    <head>
        <style>
            body { font-family: Arial, sans-serif; padding: 10px; line-height: 1.6; }
            h1, h2 { color: #2E86C1; }
            ul { margin-left: 20px; }
            li { margin-bottom: 8px; }
        </style>
    </head>
    <body>
        <h1 align="center">Introduction to Programming</h1>
        <p>
            Programming is the process of writing instructions for a computer to execute. These instructions are written using programming languages...
        </p>
        <h2>Topics Covered</h2>
        <ul>
            <li>What is Programming?</li>
            <li>Types of Programming Languages</li>
            <li>Compilation vs Interpretation</li>
            <li>Introduction to IDEs</li>
        </ul>
        <h1>What is Programming?</h1>
        <p>
            Programming involves creating algorithms and writing code to perform specific tasks...
        </p>
        <h2>Why is Programming Important?</h2>
        <ul>
            <li>Used in software and application development</li>
            <li>Essential for automation of tasks</li>
            <li>Foundational for AI, Data Science, ML</li>
            <li>Helps solve problems by creating tools</li>
            <li>Improves business efficiency</li>
        </ul>
        <h1>Types of Programming Languages</h1>
        <p>Programming languages are categorized into two types:</p>
        <h2>Low-Level Languages</h2>
        <ul>
            <li>Machine Code (Binary)</li>
            <li>Assembly Language</li>
        </ul>
        <h2>High-Level Languages</h2>
        <ul>
            <li>C, C++, Java, Python</li>
            <li>Portable across platforms</li>
        </ul>
        <h1>Compilation vs Interpretation</h1>
        <h2>Compiled Languages</h2>
        <p>Languages like C and C++ use a compiler...</p>
        <h2>Interpreted Languages</h2>
        <p>Languages like Python and JavaScript use an interpreter...</p>
        <h1>Introduction to IDEs</h1>
        <p>
            An IDE (Integrated Development Environment) helps programmers write, test, and debug their code.
        </p>
        <ul>
            <li>Visual Studio Code</li>
            <li>PyCharm</li>
            <li>Eclipse</li>
            <li>CodeBlocks</li>
        </ul>
    </body>
    </html>
""".trimIndent()


        showStudentNotes.WeekNotesContent.settings.javaScriptEnabled = true
        showStudentNotes.WeekNotesContent.loadDataWithBaseURL(null, programmingContent, "text/html", "UTF-8", null)

        showStudentNotes.backButton.setOnClickListener {
            finish()
        }
        showStudentNotes.DownloadButton.setOnClickListener {
            val dialog = AlertDialog.Builder(this@ShowNotesActivity)
                .setTitle("Download Confirmation")
                .setMessage("Do you want to download these notes?")
                .setPositiveButton("Yes") { dialog, _ ->
                    Toast.makeText(this@ShowNotesActivity, "Notes Downloaded", Toast.LENGTH_SHORT).show()
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
}