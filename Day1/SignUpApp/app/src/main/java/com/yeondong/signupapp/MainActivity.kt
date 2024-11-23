package com.example.signupapp

import android.app.DatePickerDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.Spinner
import androidx.appcompat.app.AlertDialog
import com.yeondong.signupapp.R
import java.util.Calendar

class MainActivity : AppCompatActivity() {

    private lateinit var editText: EditText
    private lateinit var spinner: Spinner
    private lateinit var buttonDate: Button
    private lateinit var editTextMessage: EditText
    private lateinit var button: Button
    private lateinit var listView: ListView
    private lateinit var arrayAdapter: ArrayAdapter<String>
    private val itemList = ArrayList<String>()

    private var name = ""
    private var sex = ""
    private var birth = ""
    private var messege = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // View 초기화
        editText = findViewById(R.id.editText)
        spinner = findViewById(R.id.spinner)
        buttonDate = findViewById(R.id.button_date)
        editTextMessage = findViewById(R.id.editText_message)
        button = findViewById(R.id.button)
        listView = findViewById(R.id.listView)

        // ListView에 사용할 ArrayAdapter 설정
        arrayAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, itemList)
        listView.adapter = arrayAdapter

        // 드롭다운에 사용할 데이터 목록
        val items = listOf("남자", "여자")

        // 어댑터 설정
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, items)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

        // 선택 이벤트 처리
        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: android.view.View, position: Int, id: Long) {
                sex = parent.getItemAtPosition(position).toString()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // 아무 것도 선택되지 않았을 때의 처리
            }
        }

        // 날짜 버튼 클릭 시 동작
        buttonDate.setOnClickListener {
            showDatePickerDialog()
        }

        // 가입 버튼 클릭 시 동작
        button.setOnClickListener {
            name = editText.text.toString()
            messege = editTextMessage.text.toString()

            if (name.isNotEmpty()) {
                // 리스트에 입력한 텍스트 추가
                itemList.add("$name | $sex | $birth | $messege")
                // 리스트 업데이트
                arrayAdapter.notifyDataSetChanged()
                // 입력 필드 초기화
                editText.text.clear()
            }
        }
    }

    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        // DatePickerDialog 생성
        val datePickerDialog = DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
            // 선택한 날짜를 텍스트뷰에 표시
            birth = "$selectedDay/${selectedMonth + 1}/$selectedYear"
            birth = "${selectedYear}년${selectedMonth + 1}월${selectedDay}일"
            buttonDate.text = birth
        }, year, month, day)

        // 다이얼로그 표시
        datePickerDialog.show()
    }
}