package com.example.scheduler_practice

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import okhttp3.*
import java.io.IOException

class SignUpActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        findViewById<Button>(R.id.signUpButton).setOnClickListener {
            val pw = findViewById<EditText>(R.id.inputPassword).text.toString()
            val pwCheck = findViewById<EditText>(R.id.inputPasswordCheck).text.toString()
            if(pw != pwCheck){
                Toast.makeText(this, "비밀번호가 서로 일치하지 않습니다.", Toast.LENGTH_SHORT).show()
            }
            else {
                val client = OkHttpClient()
                val body = FormBody.Builder()
                    .add("id", findViewById<EditText>(R.id.inputId).text.toString())
                    .add("password", findViewById<EditText>(R.id.inputPassword).text.toString())
                    .build()
                val request: Request =
                    Request.Builder().addHeader("Content-Type","application/x-www-form-urlencoded").url("http://3.143.134.73:3000/signup").post(body).build()

                client.newCall(request).enqueue(object : Callback {
                    override fun onFailure(call: Call, e: IOException) {
                        runOnUiThread {
                            Log.d("log",e.message.toString())
                            Toast.makeText(this@SignUpActivity, "회원가입 실패", Toast.LENGTH_SHORT)
                                .show()
                        }
                    }

                    override fun onResponse(call: Call, response: Response) {
                        object : Thread() {
                            override fun run() {
                                finish()
                            }
                        }.run()
                    }
                })
            }
        }
    }
}