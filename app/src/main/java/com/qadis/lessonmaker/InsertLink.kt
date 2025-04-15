package com.qadis.lessonmaker

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.qadis.lessonmaker.databinding.ActivityInsertLinkScreenBinding

class InsertLink : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val insert = ActivityInsertLinkScreenBinding.inflate(layoutInflater)
        setContentView(insert.root)

    }
}