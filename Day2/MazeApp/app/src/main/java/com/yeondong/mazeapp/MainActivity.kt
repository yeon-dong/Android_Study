package com.yeondong.mazeapp

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    // ActivityResultLauncher 선언
    private lateinit var resultLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setContentView(R.layout.activity_main)

        // ActivityResultLauncher 초기화 및 결과 처리
        resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                // 데이터를 받아옴
                val data: Intent? = result.data
                val returnedResult = data?.getBooleanExtra("result_key", false)

                showBasicDialog(returnedResult)
            }
        }

        val leftButton = findViewById<Button>(R.id.button)
        val rightButton = findViewById<Button>(R.id.button2)

        leftButton.setOnClickListener {
            val intent = Intent(this, SecondActivity::class.java)  // 이동할 액티비티 지정
            intent.putExtra("key", "left")  // "key"는 데이터를 받는 쪽에서 사용할 키
            resultLauncher.launch(intent)  // 액티비티 이동 실행
        }

        rightButton.setOnClickListener {
            val intent = Intent(this, SecondActivity::class.java)  // 이동할 액티비티 지정
            intent.putExtra("key", "right")  // "key"는 데이터를 받는 쪽에서 사용할 키
            resultLauncher.launch(intent)  // 액티비티 이동 실행
        }
    }

    // 기본 다이얼로그 표시 함수
    private fun showBasicDialog(success: Boolean?) {
        val builder = AlertDialog.Builder(this)

        if (success == true) {
            builder.setTitle("성공! 보물을 찾았어요!!")
        } else {
            builder.setTitle("꽝! 다시 시도해주세요 ㅜㅜ")
        }

        // 확인 버튼 추가
        builder.setPositiveButton("OK") { dialog, _ ->
            dialog.dismiss() // 다이얼로그 닫기
        }

        // 다이얼로그 생성 및 표시
        val dialog: AlertDialog = builder.create()
        dialog.show()

    }
}