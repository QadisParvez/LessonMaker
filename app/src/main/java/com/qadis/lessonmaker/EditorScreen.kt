package com.qadis.lessonmaker

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.qadis.lessonmaker.databinding.ActivityEditorScreenBinding
import jp.wasabeef.richeditor.RichEditor
import java.io.File
import java.io.FileOutputStream

class EditorScreen : AppCompatActivity() {

    private lateinit var editor: RichEditor
    private lateinit var bind: ActivityEditorScreenBinding
    private val sqlServerHtmlContent = """
    <h1>Microsoft SQL Server</h1>
    <h2>Introduction to SQL Server</h2>
    <p>
        Microsoft SQL Server is a powerful <strong>Relational Database Management System (RDBMS)</strong> developed by Microsoft.
        It is used to <strong>store</strong>, <strong>manage</strong>, and <strong>retrieve</strong> structured data.
        SQL Server uses <strong>Transact-SQL (T-SQL)</strong>, which is Microsoft’s proprietary extension of SQL.
        This software provides data management solutions ranging from small applications to enterprise-level systems.
    </p>
    <h3>Key Characteristics:</h3>
    <ul>
        <li>Efficiently manages structured data</li>
        <li>Uses Microsoft’s own SQL extension (T-SQL)</li>
        <li>Reliable and scalable database solution</li>
        <li>Suitable for business-level applications</li>
    </ul>
    <h2>Features and Usage</h2>
    <p>
        SQL Server provides <strong>advanced features</strong> and tools for developers and database administrators
        to manage data securely and efficiently.
        Its graphical user interface, <strong>SQL Server Management Studio (SSMS)</strong>, is very user-friendly.
        It also integrates with data warehousing and business intelligence tools.
    </p>
    <h3>Important Features:</h3>
    <ul>
        <li><strong>Backup & Recovery</strong> options for data safety</li>
        <li><strong>Replication</strong> for data duplication across systems</li>
        <li><strong>Data encryption</strong> for security</li>
        <li><strong>Stored procedures</strong> and <strong>triggers</strong> for automation</li>
        <li><strong>Indexing</strong> for fast data retrieval</li>
        <li><strong>SSIS (Integration Services)</strong> for data movement</li>
        <li><strong>SSRS (Reporting Services)</strong> for generating reports</li>
        <li><strong>SSAS (Analysis Services)</strong> for data analysis</li>
    </ul>
""".trimIndent()


    @SuppressLint("IntentReset")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        bind = ActivityEditorScreenBinding.inflate(layoutInflater)
        setContentView(bind.root)

        editor = bind.editor
        loadSqlServerLesson()

        val imagePickCode = 1002

        bind.boldBtn.setOnClickListener {
            editor.setBold()
        }
        bind.italicbtn.setOnClickListener {
            editor.setItalic()
        }
        bind.underLineBtn.setOnClickListener {
            editor.setUnderline()
        }
        bind.headingbtn2.setOnClickListener {
            editor.setHeading(2)
        }
        bind.headingbtn1.setOnClickListener {
            editor.setHeading(1)
        }
        bind.leftAlign.setOnClickListener {
            editor.setAlignLeft()
        }
        bind.rightAlign.setOnClickListener {
            editor.setAlignRight()
        }
        bind.centerAlign.setOnClickListener {
            editor.setAlignCenter()
        }
        bind.bulletPoints.setOnClickListener {
            editor.setBullets()
        }
        bind.insertImage.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/*"
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            startActivityForResult(intent, imagePickCode)
        }


        bind.insertVideo.setOnClickListener {
        }
        bind.insertLink.setOnClickListener {
            showLinkDialog()
        }
        bind.undo.setOnClickListener {
            editor.undo()
        }
        bind.redo.setOnClickListener {
            editor.redo()
        }
        bind.AddKeyword.setOnClickListener {
            highlightSelectedText()
        }
        bind.SaveBtn.setOnClickListener {
            saveDialog()
        }

    }

    private fun showLinkDialog() {
        val builder = AlertDialog.Builder(this@EditorScreen)
        val inflater = layoutInflater
        val dialogView = inflater.inflate(R.layout.activity_insert_link_screen, null)
        builder.setView(dialogView)

        val edTxtLink = dialogView.findViewById<EditText>(R.id.LinkEdText)
        val insertBtn = dialogView.findViewById<Button>(R.id.btnInsert)

        val dialog = builder.create()
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.show()

        insertBtn.setOnClickListener {
            val link = edTxtLink.text.toString().trim()
            if (link.isNotEmpty()) {
                editor.insertLink(link, "Click Here")
                dialog.dismiss()
            } else {
                edTxtLink.error = "Enter a Link"
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 1002 && resultCode == RESULT_OK && data != null) {
            val imageUri = data.data
            if (imageUri != null) {
                try {
                    val inputStream = contentResolver.openInputStream(imageUri)
                    val originalBitmap = BitmapFactory.decodeStream(inputStream)
                    inputStream?.close()
                    val resizedBitmap = Bitmap.createScaledBitmap(originalBitmap, 50, 50, true)

                    val file = File(cacheDir, "resized_image_${System.currentTimeMillis()}.png")
                    val outputStream = FileOutputStream(file)
                    resizedBitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
                    outputStream.flush()
                    outputStream.close()

                    val resizedImageUri = FileProvider.getUriForFile(
                        this,
                        "$packageName.fileprovider",
                        file
                    )

                    editor.insertImage(resizedImageUri.toString(), "Resized Image")

                } catch (e: Exception) {
                    e.printStackTrace()
                    Toast.makeText(this, "Failed to process the image.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }



    private fun loadSqlServerLesson() {
        editor.html = sqlServerHtmlContent
    }


    private fun saveDialog() {
        val builder = AlertDialog.Builder(this@EditorScreen)
        val inflate = layoutInflater
        val dialogView = inflate.inflate(R.layout.activity_save_dialog, null)
        builder.setView(dialogView)


        val edTxtCrs = dialogView.findViewById<EditText>(R.id.edTextCrs)
        val edTxtWeek = dialogView.findViewById<EditText>(R.id.edTextWeekNo)
        val saveBtn = dialogView.findViewById<Button>(R.id.SaveDialogBtn)

        val dialog = builder.create()
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.show()

        saveBtn.setOnClickListener {
            val crs = edTxtCrs.text.toString().trim()
            val week = edTxtWeek.text.toString().trim()
        }

    }

    private fun highlightSelectedText() {
        val jsCode = """
        var selection = window.getSelection();
        if (selection.rangeCount > 0) {
            var range = selection.getRangeAt(0);
            var span = document.createElement("span");
            span.style.color = "red";  // ✅ Text Color
            range.surroundContents(span);
        }
    """.trimIndent()
        bind.editor.evaluateJavascript(jsCode, null)  // ✅ JavaScript execute karega WebView me
    }
    




}