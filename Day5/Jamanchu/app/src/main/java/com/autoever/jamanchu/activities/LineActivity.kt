package com.autoever.jamanchu.activities

import android.os.Bundle
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.autoever.jamanchu.R
import com.autoever.jamanchu.api.RetrofitInstance
import com.autoever.jamanchu.models.Line
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch

class LineActivity : AppCompatActivity() {
    private val firebaseAuth = FirebaseAuth.getInstance()
    private var lineId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_line)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val editText = findViewById<EditText>(R.id.editText)
        val textViewComplete = findViewById<TextView>(R.id.textViewComplete)
        textViewComplete.setOnClickListener {
            val content = editText.text.toString()
            if (lineId == null) {
                addLine(content)
            } else {
                updateLine(lineId!!, content)
            }
        }

        // 라인 수정
        lineId = intent.getStringExtra("lineId")
        lineId?.let {
            val lineContent: String = intent.getStringExtra("lineContent")!!
            editText.setText(lineContent)
        }

    }

    fun updateLine(id: String, text: String) {
        val updatedLine = Line(id = id, user = firebaseAuth.currentUser?.uid!!, line = text)

        lifecycleScope.launch {
            try {
                val response = RetrofitInstance.api.updateLine(id, updatedLine)
                if (response.isSuccessful) {
                    setResult(RESULT_OK)
                    finish()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun addLine(text: String) {
        val userId = firebaseAuth.currentUser?.uid ?: return
        val newLine = Line(id= "", user = userId, line = text)
        lifecycleScope.launch {
            try {
                val response = RetrofitInstance.api.createLine(newLine)
                if (response.isSuccessful && response.body() != null) {
                    setResult(RESULT_OK) // 작성 완료 결과 설정
                    finish() // 액티비티 종료
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}