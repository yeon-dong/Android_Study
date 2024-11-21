package com.yeondong.reservation

import android.app.DatePickerDialog
import android.icu.util.Calendar
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.get

class MainActivity : AppCompatActivity() {
    var location: String = ""
    var date: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val editText = findViewById<EditText>(R.id.editText1)
        val spinner = findViewById<Spinner>(R.id.spinner)
        val buttonDate = findViewById<Button>(R.id.buttonDate)
        val button2 = findViewById<Button>(R.id.button2)

        val locationList = listOf(
            "서울", "양양", "대전", "부산", "광주"
        )

        //스피너 어댑터 설정
        val adapter = ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, locationList)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_item)
        spinner.adapter = adapter

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                location = p0!!.getItemIdAtPosition(p2).toString()
                Toast.makeText(application, location, Toast.LENGTH_SHORT).show()
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {

            }
        }

        buttonDate.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            // DatePickerDialog 생성
            val datePickerDialog =
                DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
                    // 선택한 날짜를 버튼에 표시
                    date = "${selectedDay}/${selectedMonth + 1}/${selectedYear}"
                    buttonDate.text = date
                }, year, month, day)

            //다이얼로그 표시
            datePickerDialog.show()
        }
    }
}