package com.example.flowerbread

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.RadioGroup

class MoldActivity : AppCompatActivity() {

    lateinit var bread: Bread
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mold)

        val imageView = findViewById<ImageView>(R.id.imageView)

        // 빵 모양 선택
        val radioGroupMold = findViewById<RadioGroup>(R.id.radioGroupMold)
        val radioButtonFish = findViewById<RadioButton>(R.id.radioButtonFish)
        val radioButtonFlower = findViewById<RadioButton>(R.id.radioButtonFlower)
        radioButtonFish.setOnClickListener {
            val selectedId = radioGroupMold.checkedRadioButtonId
            if (selectedId != -1) {
                imageView.setImageResource(R.drawable.fish_mold)
                bread = FishBread()
                bread.setShape()
            }
        }
        radioButtonFlower.setOnClickListener {
            val selectedId = radioGroupMold.checkedRadioButtonId
            if (selectedId != -1) {
                imageView.setImageResource(R.drawable.flower_mold)
                bread = FlowerBread()
                bread.setShape()
            }
        }

        // 소스 선택
        val radioGroupSauce = findViewById<RadioGroup>(R.id.radioGroupSauce)
        val radioButtonBean = findViewById<RadioButton>(R.id.radioButtonBean)
        val radioButtonCream = findViewById<RadioButton>(R.id.radioButtonCream)
        radioButtonBean.setOnClickListener {
            val selectedId = radioGroupSauce.checkedRadioButtonId
            if (selectedId != -1) {
                bread.putSauce("팥앙금")
            }
        }
        radioButtonCream.setOnClickListener {
            val selectedId = radioGroupSauce.checkedRadioButtonId
            if (selectedId != -1) {
                bread.putSauce("슈크림")
            }
        }

        // 만들기 버튼
        val buttonMake = findViewById<Button>(R.id.buttonMake)
        buttonMake.setOnClickListener {
            // 결과를 반환하는 코드
            val resultIntent = Intent()
            resultIntent.putExtra("shape", bread.shape) // 결과 데이터를 인텐트에 담기
            resultIntent.putExtra("sauce", bread.sauce) // 결과 데이터를 인텐트에 담기
            setResult(Activity.RESULT_OK, resultIntent) // 결과 코드와 인텐트 설정
            finish() // 액티비티 종료
        }

        // 취소 버튼
        val buttonCancel = findViewById<Button>(R.id.buttonCancel)
        buttonCancel.setOnClickListener {
            finish()
        }
    }
}