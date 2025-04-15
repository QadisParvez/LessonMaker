package com.qadis.lessonmaker

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.qadis.lessonmaker.databinding.ActivityEditorScreenBinding
import jp.wasabeef.richeditor.RichEditor

class EditorScreen : AppCompatActivity() {

    private lateinit var editor: RichEditor
    private lateinit var bind: ActivityEditorScreenBinding

    @SuppressLint("IntentReset")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        bind = ActivityEditorScreenBinding.inflate(layoutInflater)
        setContentView(bind.root)

        editor = bind.editor
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
                val imagePath = imageUri.toString()
                editor.insertImage(imagePath, "Selected Image")
            }
        }
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