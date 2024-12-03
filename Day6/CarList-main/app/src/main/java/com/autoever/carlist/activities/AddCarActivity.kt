package com.autoever.carlist.activities

import android.os.Bundle
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.autoever.carlist.Car
import com.autoever.carlist.R
import com.autoever.carlist.api.RetrofitInstance
import kotlinx.coroutines.launch

class AddCarActivity : AppCompatActivity() {
    private var carId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_add_car)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val editText = findViewById<EditText>(R.id.editText)

        // 수정일 때
        carId = intent.getStringExtra("carId")
        carId?.let {
            val carContent: String = intent.getStringExtra("carContent")!!
            editText.setText(carContent)
        }

        val textViewTitle = findViewById<TextView>(R.id.textViewTitle)
        if (carId == null) {
            textViewTitle.text = "등록"
        } else {
            textViewTitle.text = "수정"
        }

        val textViewComplete = findViewById<TextView>(R.id.textViewComplete)
        textViewComplete.setOnClickListener {
            val content = editText.text.toString()
            if (carId == null) {
                addCar(content)
            } else {
                updateCar(carId!!, content)
            }
        }
    }

    fun updateCar(id: String, text: String) {
        val updatedCar = Car(id = id, name = text)

        lifecycleScope.launch {
            try {
                val response = RetrofitInstance.api.update(id, updatedCar)
                if (response.isSuccessful) {
                    setResult(RESULT_OK)
                    finish()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun addCar(text: String) {
        val newCar = Car(id = "", name = text)
        lifecycleScope.launch {
            try {
                val response = RetrofitInstance.api.create(newCar)
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