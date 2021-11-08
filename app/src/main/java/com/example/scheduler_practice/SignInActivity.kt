package com.example.scheduler_practice

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import okhttp3.*
import java.io.IOException

class SignInActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signin)

        findViewById<Button>(R.id.goToSignUpButton).setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }

        findViewById<Button>(R.id.logInButton).setOnClickListener {
            val client = OkHttpClient()
            val body = FormBody.Builder()
                .add("id", findViewById<EditText>(R.id.userId).text.toString())
                .add("password", findViewById<EditText>(R.id.userPassword).text.toString()).build()

            val request : Request = Request.Builder().addHeader("Content-Type","application/x-www-form-urlencoded").url("http://3.143.134.73:3000/signin").post(body).build()

            client.newCall(request).enqueue(object: Callback {
                override fun onFailure(call: Call, e: IOException) {
                    runOnUiThread {
                        Toast.makeText(
                            this@SignInActivity,
                            "인터넷 연결이 불안정합니다. 다시 시도해주세요.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onResponse(call: Call, response: Response) {
                    object: Thread() {
                        override fun run() {
                            Log.d("log", response.toString())
                            if (response.code == 200) {
                                val intent = Intent(this@SignInActivity, MainActivity::class.java)
                                startActivity(intent)
                                finish()
                            } else if (response.code == 201) {
                                runOnUiThread {
                                    Toast.makeText(
                                        this@SignInActivity,
                                        "아이디 또는 비밀번호가 일치하지 않습니다.",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        }
                    }.run()
                }

            })
        }
    }
}