package com.yeondong.matchingapp2

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import kotlin.random.Random

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val editText1 = findViewById<EditText>(R.id.editText1)
        val editText2 = findViewById<EditText>(R.id.editText2)
        val button = findViewById<TextView>(R.id.textView3)
        button.setOnClickListener {
            if(editText1.text.isEmpty()|| editText2.text.isEmpty()){
                Toast.makeText(applicationContext, "이름을 입력해주세요.", Toast.LENGTH_SHORT).show()

                return@setOnClickListener
            }else{
                val user1 = editText1.text.toString()
                val user2 = editText2.text.toString()


                val percentage = calculateMatchingProbability(user1, user2)
                Log.d("MainActivity", "매칭 확률은 $percentage% 입니다.")

                showDefaultDialog(user1, user2, percentage)
            }
        }
    }

    fun showDefaultDialog(user1: String, user2: String, percentage: Int) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("커플 매칭확률")
        builder.setMessage("${user1}과 ${user2}의 매칭 확률은 $percentage% 입니다.")
        builder.setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
        val dialog = builder.create()
        dialog.show()
    }

    fun calculateMatchingProbability(name1: String, name2: String): Int {
        // 두 이름의 길이를 기반으로 최대 점수를 설정합니다.
        val maxLength = Math.max(name1.length, name2.length)
        if (maxLength == 0) return 0 // 둘 다 빈 문자열인 경우

        // 공통 문자를 계산합니다.
        val commonChars = name1.toSet().intersect(name2.toSet()).size

        // 유사성 점수를 계산합니다.
        val score = (commonChars.toDouble() / maxLength) * 100

        return score.toInt() // 정수형으로 반환
    }
}