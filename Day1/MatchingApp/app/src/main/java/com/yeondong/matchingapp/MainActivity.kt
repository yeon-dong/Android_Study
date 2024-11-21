package com.yeondong.matchingapp

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val editText1 = findViewById<EditText>(R.id.editText1)
        val button = findViewById<Button>(R.id.button)
        button.setOnClickListener{
            var percentage: Int = 50
            println("매칭 확률은 $percentage% 입니다.")
        }
    }
}