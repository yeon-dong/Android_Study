package com.yeondong.mazeapp

import android.os.Bundle
import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog

class SecondActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second)

        val receivedData = intent.getStringExtra("key")  // "key"는 데이터를 보낼 때 사용한 키와 동일해야 함

        val buttonUp = findViewById<Button>(R.id.button)
        val buttonLeft = findViewById<Button>(R.id.button2)
        val buttonRight = findViewById<Button>(R.id.button3)
        val buttonDown = findViewById<Button>(R.id.button4)

        // 버튼 클릭 시 동작
        buttonUp.setOnClickListener {
            if (receivedData != null) {
                checkAnswer(receivedData, "up")
            }
        }

        // 버튼 클릭 시 동작
        buttonLeft.setOnClickListener {
            if (receivedData != null) {
                checkAnswer(receivedData, "left")
            }
        }

        // 버튼 클릭 시 동작
        buttonRight.setOnClickListener {
            if (receivedData != null) {
                checkAnswer(receivedData, "right")
            }
        }

        // 버튼 클릭 시 동작
        buttonDown.setOnClickListener {
            if (receivedData != null) {
                checkAnswer(receivedData, "down")
            }
        }
    }

    // 첫 번째, 두 번째 방향이 모두 맞는지 확인하는 함수
    private fun checkAnswer(firstDirection: String, secondDirection: String) {
        val resultIntent = Intent()
        if (firstDirection == "right" && secondDirection == "down") {
            resultIntent.putExtra("result_key", true)
        } else {
            resultIntent.putExtra("result_key", false)
        }
        setResult(Activity.RESULT_OK, resultIntent)
        finish()  // Activity B 종료
    }
}
