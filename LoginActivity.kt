package com.example.studyrom

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val etStudentId = findViewById<EditText>(R.id.etStudentId)
        val btnLogin = findViewById<Button>(R.id.btnLogin)

        btnLogin.setOnClickListener {
            val studentId = etStudentId.text.toString().trim()

            if (studentId.length == 8) {
                // 학번을 들고 메인 화면(예약 화면)으로 이동!
                val intent = Intent(this, MainActivity::class.java)
                intent.putExtra("STUDENT_ID", studentId)
                startActivity(intent)
                finish() // 로그인 화면은 뒤로가기 해도 안 나오게 닫기
            } else {
                Toast.makeText(this, "정확한 학번 8자리를 입력해주세요.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}