package com.yeondong.matchingapp

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
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
        val button = findViewById<Button>(R.id.button)
        button.setOnClickListener{
            var percentage: Int = Random.nextInt(0,101)
            //println("매칭 확률은 $percentage% 입니다.")
            Log.d("MainActivity", "매칭 확률은 $percentage% 입니다.")

            showDefaultDialog(editText1.text.toString(),editText2.text.toString(),percentage)
        }
    }

    fun showDefaultDialog(user1: String, user2: String, percentage: Int){
        var builder = AlertDialog.Builder(this)
        builder.setTitle("커플 매칭확률")
        builder.setMessage("${user1}과 ${user2}의 매칭 확률은 $percentage% 입니다.")
        builder.setPositiveButton("OK"){
            dialog, _ -> dialog.dismiss() //다이얼로그 없애기
        }
        val dialog = builder.create()
        dialog.show()
    }
}