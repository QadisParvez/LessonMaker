package com.qadis.lessonmaker

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
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
                loginUser(this@LoginPage, userID, password)
            } else {
                Toast.makeText(
                    this@LoginPage,
                    "Enter All Required Fields To Login",
                    Toast.LENGTH_SHORT
                ).show()
            }

        }
    }

}


private fun loginUser(activity: AppCompatActivity, userId: String, password: String) {
    val trimmedUserId = userId.trim()
    val trimmedPassword = password.trim()

    println("🔍 Debug: Sending Request - ID: $trimmedUserId, Password: $trimmedPassword")

    RetrofitClient.instance.getUser(trimmedUserId, trimmedPassword)
        .enqueue(object : Callback<UserResponse> {
            override fun onResponse(call: Call<UserResponse>, response: Response<UserResponse>) {
                println("✅ Response Code: ${response.code()}")

                if (response.isSuccessful && response.body() != null) {
                    val loginResponse = response.body()!!
                    println("✅ Full API Response: $loginResponse")

                    if (loginResponse.success) {
                        val roleMessage = when (loginResponse.role) {
                            3 -> "Logged in as Student"
                            2 -> "Logged in as Teacher"
                            else -> "Unknown user type"
                        }

                        Toast.makeText(activity, roleMessage, Toast.LENGTH_SHORT).show()

                        val intent = when (loginResponse.role) {
                            3 -> Intent(activity, StudentDashboard::class.java)
                            2 -> Intent(activity, TeacherDashboard::class.java)
                            else -> null
                        }

                        intent?.putExtra("UserID", userId)
                        intent?.putExtra("UserName", loginResponse.name)
                        intent?.let { activity.startActivity(it) }

                    } else {
                        Toast.makeText(activity, "Invalid Credentials", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    println("🚨 Backend returned error: ${response.errorBody()?.string()}")
                    Toast.makeText(activity, "Login failed! Try again.", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<UserResponse>, t: Throwable) {
                println("🚨 Network Error: ${t.message}")
                Toast.makeText(activity, "Network Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
}
