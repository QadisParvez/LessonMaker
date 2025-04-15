package com.qadis.lessonmaker

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.qadis.lessonmaker.databinding.ActivitySaveDialogBinding

class SaveDialog : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val saveDialog = ActivitySaveDialogBinding.inflate(layoutInflater)
        setContentView(saveDialog.root)

    }
}