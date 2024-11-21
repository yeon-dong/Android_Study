package com.yeondong.memoapp3

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val arrayList: ArrayList<String> = ArrayList<String>()
        arrayList.add("빨래 하기")
        arrayList.add("운동 하기")
        arrayList.add("점심 먹기")

        val editText: EditText = findViewById(R.id.editText)

        val listView: ListView = findViewById(R.id.listView)

        val arrayAdapter: ArrayAdapter<String> = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, arrayList)
        listView.adapter = arrayAdapter

        val button: Button = findViewById(R.id.button)
        button.setOnClickListener{
            arrayList.add("${editText.text}") //toString 해도 됨
            arrayAdapter.notifyDataSetChanged()
        }


    }
}