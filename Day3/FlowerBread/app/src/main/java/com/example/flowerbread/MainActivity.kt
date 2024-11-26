package com.example.flowerbread

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {
    private lateinit var getResult: ActivityResultLauncher<Intent>

    val breadList: MutableList<Bread> = mutableListOf()

    private lateinit var imageViews: ArrayList<ImageView>
    private lateinit var textViews: ArrayList<TextView>

    var today = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        getResult = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                // 결과 처리
                val shape = data?.getStringExtra("shape")
                val sauce = data?.getStringExtra("sauce")

                val bread = if (shape == "Fish") FishBread() else FlowerBread()
                bread.setShape()
                bread.putSauce(sauce!!)
                breadList.add(bread)

                for (imageView in imageViews) {
                    imageView.setImageDrawable(null)
                }

                for (textView in textViews) {
                    textView.text = ""
                }

                // 빵 진열
                for ((i, bread1) in breadList.withIndex()) {
                    if (bread1.shape == "Fish") {
                        imageViews[i].setImageResource(R.drawable.fish)
                    } else {
                        imageViews[i].setImageResource(R.drawable.flower)
                    }
                    textViews[i].text = bread1.sauce
                }
            }
        }

        val buttonMake = findViewById<Button>(R.id.buttonMake)
        buttonMake.setOnClickListener {
            if (breadList.size < 9) {
                val intent = Intent(this, MoldActivity::class.java)
                getResult.launch(intent)
            } else {
                Toast.makeText(this, "빵은 최대 9개까지 만들어 놓을 수 있어요.", Toast.LENGTH_SHORT).show()
            }
        }

        // 이미지뷰 레이아웃 ID를 배열로 저장
        val numberImageViews = arrayOf(
            R.id.imageView0, R.id.imageView1, R.id.imageView2, R.id.imageView3,
            R.id.imageView4, R.id.imageView5, R.id.imageView6, R.id.imageView7,
            R.id.imageView8
        )

        imageViews = ArrayList()
        for (id in numberImageViews) {
            val imageView = findViewById<ImageView>(id) // 각 ID에 해당하는 ImageView를 초기화
            imageViews.add(imageView) // ArrayList에 추가
        }

        // 텍스트뷰 레이아웃 ID를 배열로 저장
        val numberTextViews = arrayOf(
            R.id.textView0, R.id.textView1, R.id.textView2, R.id.textView3,
            R.id.textView4, R.id.textView5, R.id.textView6, R.id.textView7,
            R.id.textView8
        )

        textViews = ArrayList()
        for (id in numberTextViews) {
            val textView = findViewById<TextView>(id) // 각 ID에 해당하는 ImageView를 초기화
            textViews.add(textView) // ArrayList에 추가
        }

        // 판매
        val textViewToday = findViewById<TextView>(R.id.textViewToday)
        val buttonSell = findViewById<Button>(R.id.buttonSell)
        buttonSell.setOnClickListener {
            var earn = 0
            for (bread2 in breadList) {
                earn += bread2.price
            }
            showBasicDialog(earn)
            today += earn
            textViewToday.text = "${today}원"

            // 진열대 초기화
            breadList.clear()
            for (imageView in imageViews) {
                imageView.setImageDrawable(null)
            }
            for (textView in textViews) {
                textView.text = ""
            }
        }
    }

    // 판매 금액 알림
    private fun showBasicDialog(earn: Int) {
        val builder = AlertDialog.Builder(this)

        builder.setTitle("판매 완료!!")
        builder.setMessage("${earn}원을 벌었어요!")

        // 확인 버튼 추가
        builder.setPositiveButton("OK") { dialog, _ ->
            dialog.dismiss() // 다이얼로그 닫기
        }

        // 다이얼로그 생성 및 표시
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }
}