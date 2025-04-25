package com.qadis.lessonmaker

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.qadis.lessonmaker.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val bind = ActivityMainBinding.inflate(layoutInflater)
        setContentView(bind.root)

        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this, LoginPage::class.java)
            startActivity(intent)
            finish()
        }, 3000)
    }
}
