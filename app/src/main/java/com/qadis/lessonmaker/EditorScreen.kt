    package com.qadis.lessonmaker

    import android.annotation.SuppressLint
    import android.content.Intent
    import android.graphics.Bitmap
    import android.graphics.BitmapFactory
    import android.net.Uri
    import android.os.Bundle
    import android.util.Base64
    import android.util.Log
    import android.widget.Button
    import android.widget.EditText
    import android.widget.Toast
    import androidx.activity.enableEdgeToEdge
    import androidx.appcompat.app.AlertDialog
    import androidx.appcompat.app.AppCompatActivity
    import com.qadis.lessonmaker.databinding.ActivityEditorScreenBinding
    import com.qadis.lessonmaker.model.*
    import com.qadis.lessonmaker.api.RetrofitClient
    import jp.wasabeef.richeditor.RichEditor
    import java.io.ByteArrayOutputStream

    
    
    class EditorScreen : AppCompatActivity() {


        private lateinit var editor: RichEditor
        private lateinit var bind: ActivityEditorScreenBinding
        private val selectedKeywords = mutableListOf<String>()
        private val imagePickCode = 1002


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

            // Formatting buttons
            bind.boldBtn.setOnClickListener { editor.setBold() }
            bind.italicbtn.setOnClickListener { editor.setItalic() }
            bind.underLineBtn.setOnClickListener { editor.setUnderline() }
            bind.headingbtn1.setOnClickListener { editor.setHeading(1) }
            bind.headingbtn2.setOnClickListener { editor.setHeading(2) }
            bind.leftAlign.setOnClickListener { editor.setAlignLeft() }
            bind.centerAlign.setOnClickListener { editor.setAlignCenter() }
            bind.rightAlign.setOnClickListener { editor.setAlignRight() }
            bind.bulletPoints.setOnClickListener { editor.setBullets() }
            bind.undo.setOnClickListener { editor.undo() }
            bind.redo.setOnClickListener { editor.redo() }

            // Buttons
            bind.insertImage.setOnClickListener { pickImage() }
            bind.insertLink.setOnClickListener { showLinkDialog() }
            bind.AddKeyword.setOnClickListener { highlightSelectedText() }
            bind.SaveBtn.setOnClickListener { saveLessonToServer() }
        }

        private fun loadSqlServerLesson() {
            editor.html = sqlServerHtmlContent
        }

        private fun pickImage() {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/*"
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            startActivityForResult(intent, imagePickCode)
        }

        private fun showLinkDialog() {
            val builder = AlertDialog.Builder(this)
            val dialogView = layoutInflater.inflate(R.layout.activity_insert_link_screen, null)
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

        private fun highlightSelectedText() {
            val jsCode = """
            (function() {
                var selection = window.getSelection();
                if (selection.rangeCount > 0) {
                    var range = selection.getRangeAt(0);
                    var selectedText = range.toString();
                    var span = document.createElement("span");
                    span.style.color = "red";
                    span.setAttribute("data-keyword", selectedText);
                    range.surroundContents(span);
                    return selectedText;
                }
                return "";
            })();
        """.trimIndent()

            editor.evaluateJavascript(jsCode) { result ->
                val keyword = result.trim('"')
                if (keyword.isNotEmpty() && !selectedKeywords.contains(keyword)) {
                    selectedKeywords.add(keyword)
                    Toast.makeText(this, "Keyword added: $keyword", Toast.LENGTH_SHORT).show()
                }
            }
        }

        private fun saveLessonToServer() {
            val courseCode = bind.CourseCode.text.toString().trim()
            val weekNo = bind.WeekNo.text.toString().trim()
            val content = editor.html

            if (courseCode.isEmpty()) {
                bind.CourseCode.error = "Enter Course Code"
                return
            }

            if (weekNo.isEmpty()) {
                bind.WeekNo.error = "Enter Week Number"
                return
            }

            val lessonId = 1 // Set dynamically if needed

            val keywords = selectedKeywords.map {
                KeywordData(lessonId = lessonId, keyword = it)
            }

            val tocList = selectedKeywords.map {
                TOCData(title = it, link = "#")
            }

            val pageData = PageData(
                lessonId = lessonId,
                pageNumber = weekNo.toInt(),
                content = content,
                tocList = tocList,
                keywords = keywords
            )

            val request = LessonRequest(pages = listOf(pageData))

            val gson = com.google.gson.Gson()
            val jsonString = gson.toJson(request)
            Log.d("LessonRequestJSON", jsonString)

            RetrofitClient.instance.createLesson(request)
                .enqueue(object : retrofit2.Callback<CreateLessonResponse> {
                    override fun onResponse(
                        call: retrofit2.Call<CreateLessonResponse>,
                        response: retrofit2.Response<CreateLessonResponse>
                    ) {
                        if (response.isSuccessful && response.body()?.isSuccess == true) {
                            Toast.makeText(this@EditorScreen, "Lesson Saved Successfully", Toast.LENGTH_SHORT).show()
                            finish()
                        } else {
                            val errorBody = response.errorBody()?.string()
                            Log.e("LessonSaveError", "Error: $errorBody")
                            Toast.makeText(this@EditorScreen, "Failed: $errorBody", Toast.LENGTH_LONG).show()
                        }
                    }

                    override fun onFailure(call: retrofit2.Call<CreateLessonResponse>, t: Throwable) {
                        Log.e("LessonSaveFailure", "Throwable: ${t.localizedMessage}")
                        Toast.makeText(this@EditorScreen, "Error: ${t.localizedMessage}", Toast.LENGTH_SHORT).show()
                    }
                })
        }

        override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
            super.onActivityResult(requestCode, resultCode, data)

            if (requestCode == imagePickCode && resultCode == RESULT_OK && data != null) {
                val imageUri: Uri? = data.data
                imageUri?.let {
                    try {
                        val inputStream = contentResolver.openInputStream(it)
                        val originalBitmap = BitmapFactory.decodeStream(inputStream)
                        inputStream?.close()

                        val resizedBitmap = Bitmap.createScaledBitmap(originalBitmap, 300, 300, true)

                        val byteArrayOutputStream = ByteArrayOutputStream()
                        resizedBitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
                        val byteArray = byteArrayOutputStream.toByteArray()

                        val base64Image = Base64.encodeToString(byteArray, Base64.DEFAULT)
                        val base64Url = "data:image/png;base64,$base64Image"

                        editor.insertImage(base64Url, "Base64 Image")
                    } catch (e: Exception) {
                        e.printStackTrace()
                        Toast.makeText(this, "Failed to process image", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }