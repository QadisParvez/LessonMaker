package com.qadis.lessonmaker

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.qadis.lessonmaker.api.RetrofitClient
import com.qadis.lessonmaker.api.UserResponse
import com.qadis.lessonmaker.databinding.ActivityLoginPageBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginPage : AppCompatActivity() {
    @SuppressLint("ShowToast")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val bind = ActivityLoginPageBinding.inflate(layoutInflater)
        setContentView(bind.root)
        bind.btnLogin.elevation = 10f

        bind.btnLogin.setOnClickListener {
            val userID = bind.username.text.toString().trim()
            val password = bind.password.text.toString().trim()

            if (userID.isNotEmpty() && password.isNotEmpty()) {
                bind.progressBar.visibility = View.VISIBLE
                loginUser(userID, password, bind)
            } else {
                Toast.makeText(this, "Enter All Required Fields To Login", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun loginUser(userId: String, password: String, bind: ActivityLoginPageBinding) {
        val trimmedUserId = userId.trim()
        val trimmedPassword = password.trim()

        RetrofitClient.instance.getUser(trimmedUserId, trimmedPassword)
            .enqueue(object : Callback<UserResponse> {
                override fun onResponse(call: Call<UserResponse>, response: Response<UserResponse>) {
                    bind.progressBar.visibility = View.GONE // ❌ Hide spinner

                    if (response.isSuccessful && response.body() != null) {
                        val loginResponse = response.body()!!

                        if (loginResponse.success) {
                            val roleMessage = when (loginResponse.role) {
                                3 -> "Logged in as Student"
                                2 -> "Logged in as Teacher"
                                else -> "Unknown user type"
                            }

                            Toast.makeText(this@LoginPage, roleMessage, Toast.LENGTH_SHORT).show()

                            val intent = when (loginResponse.role) {
                                3 -> Intent(this@LoginPage, StudentDashboard::class.java)
                                2 -> Intent(this@LoginPage, TeacherDashboard::class.java)
                                else -> null
                            }

                            intent?.putExtra("UserID", userId)
                            intent?.putExtra("UserName", loginResponse.name)
                            intent?.let {
                                startActivity(it)
                                finish()
                            }

                        } else {
                            Toast.makeText(this@LoginPage, "Invalid Credentials", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(this@LoginPage, "Login failed! Try again.", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<UserResponse>, t: Throwable) {
                    bind.progressBar.visibility = View.GONE
                    Toast.makeText(this@LoginPage, "Network Error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }
}