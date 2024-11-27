package com.example.stopwatch

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    private var time = 0.0
    private val lap = mutableListOf<String>()
    private var running = false
    private lateinit var handler: Handler
    private lateinit var runnable: Runnable
    private lateinit var adapter: ArrayAdapter<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // ListView 가져오기
        val listView = findViewById<ListView>(R.id.listView)

        // ArrayAdapter 생성 및 설정
        adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, lap)
        listView.adapter = adapter

        val textView = findViewById<TextView>(R.id.textView)
        val buttonStart = findViewById<Button>(R.id.start)
        val buttonPause = findViewById<Button>(R.id.pause)
        val buttonReset = findViewById<Button>(R.id.reset)
        val buttonLap = findViewById<Button>(R.id.lap)

        handler = Handler(Looper.getMainLooper())

        runnable = object : Runnable {
            override fun run() {
                if (running) {
                    time += 0.01
                    textView.text = String.format("%.2f", time)
                    handler.postDelayed(this, 10)
                }
            }
        }

        buttonStart.setOnClickListener {
            running = true
            handler.post(runnable)
        }

        buttonPause.setOnClickListener {
            running = false
        }

        buttonReset.setOnClickListener {
            running = false
            time = 0.0
            textView.text = String.format("%.2f", time)
            lap.clear() // 랩 타임 리스트 초기화
            adapter.notifyDataSetChanged() // 리스트뷰 업데이트
        }

        buttonLap.setOnClickListener {
            lap.add(String.format("%.2f", time)) // 랩 타임 추가
            adapter.notifyDataSetChanged() // 리스트뷰 업데이트
        }

        // 고차함수로 버튼 클릭 리스너 설정
//        setButtonClickListener(buttonStart) { startTimer() }
//        setButtonClickListener(buttonPause) { pauseTimer() }
//        setButtonClickListener(buttonReset) { resetTimer(textView) }
//        setButtonClickListener(buttonLap) { addLap() }
    }

    /*// 고차함수를 사용한 버튼 클릭 설정
    private fun setButtonClickListener(button: Button, action: () -> Unit) {
        button.setOnClickListener { action() }
    }

    // 타이머 시작
    private fun startTimer() {
        running = true
        handler.post(runnable)
    }

    // 타이머 일시정지
    private fun pauseTimer() {
        running = false
    }

    // 타이머 리셋
    private fun resetTimer(textView: TextView) {
        running = false
        time = 0.0
        textView.text = String.format("%.2f", time)
        lap.clear() // 랩 타임 리스트 초기화
        adapter.notifyDataSetChanged() // 리스트뷰 업데이트
    }

    // 랩 타임 추가
    private fun addLap() {
        lap.add(String.format("%.2f", time)) // 랩 타임 추가
        adapter.notifyDataSetChanged() // 리스트뷰 업데이트
    }*/
}