package com.example.randommenuapp

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class SubActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sub)
        val textView4 = findViewById<TextView>(R.id.textView4)
        val textView6 = findViewById<TextView>(R.id.textView6)
        val button2 = findViewById<Button>(R.id.button2)
        val category = intent.getStringExtra("category")

        data class Restaurant(val name: String, val url: String, val menu: String)

        // 한식 음식점 리스트
        val KoreanFood = listOf(
            Restaurant("더푸드스케치", "https://naver.me/FWJ6QljE", "1인 식사권 (부페식) 7500원"),
            Restaurant("더푸드스케치", "https://naver.me/FWJ6QljE", "1인 식사권 (부페식) 7500원"),
            Restaurant("더푸드스케치", "https://naver.me/FWJ6QljE", "1인 식사권 (부페식) 7500원"),
            Restaurant("더푸드스케치", "https://naver.me/FWJ6QljE", "1인 식사권 (부페식) 7500원"),
            Restaurant("더푸드스케치", "https://naver.me/FWJ6QljE", "1인 식사권 (부페식) 7500원"),
            Restaurant("백채김치찌개", "https://naver.me/5xj0Sbad", "돼지김치찌개 1인분 10000원"),
            Restaurant("망향비빔국수", "https://naver.me/GVAN8D4A", "비빔국수 7000원"),
            Restaurant("오늘된장", "https://naver.me/GjZFAE5e", "고기정식 10,500원"),
            Restaurant("명동할머니국수", "https://naver.me/FuQAaMCq", "초계국수 7000원"),
            Restaurant("뚱스분식", "https://naver.me/501rWBeI", "참치김밥 4500원"),
            Restaurant("신의주찹쌀순대", "https://naver.me/xv0C4UXt", "순대국 8000원"),
            Restaurant("본가왕뼈감자탕", "https://naver.me/GPrFskXP", "뼈다귀해장국 10000원"),
            Restaurant("종로김밥", "https://naver.me/59jOe2Bd", "참치김밥 5000원"),
            Restaurant("순두부와청국장", "https://naver.me/F36TIG1E", "얼큰순두부 8000원"),
            Restaurant("송탄구가네부대찌개", "https://naver.me/GQ4aW6R9", "프리미엄부대찌개 10000원"),
            Restaurant("카레업자", "https://naver.me/56IHdH6u", "수제카츠카레 9900원"),
            Restaurant("가츠나베1.186", "https://naver.me/F9QphY6U", "김치가츠나베 10500원"),
            Restaurant("김가네", "https://naver.me/FyeFcnQG", "참치김밥 5000원")
        )

        //중식 음식점 리스트
        val ChineseFood = listOf(
            Restaurant("고구려짬뽕", "https://naver.me/xHnIsykk", "고기짬뽕 9500원"),
            Restaurant("만다린", "https://naver.me/5X9DeaSV", "자장면 7000원"),
            Restaurant("라홍방마라탕", "https://naver.me/GwEkYHA4", "마라탕 8000원"),
            Restaurant("부산간짜장", "https://naver.me/xXxqfPiF", "부산간짜장 8500원")
        )

        // 양식 음식점 리스트
        val WesternFood = listOf(
            Restaurant("KFC", "https://naver.me/xyUlH023", "징거BLT세트 9500원"),
            Restaurant("왓더버거", "https://naver.me/5s32kOuK", "시그니처버거세트 10500원"),
            Restaurant("맥도날드", "https://naver.me/FVPB8mXd", "1955버거 세트 7800원"),
            Restaurant("서브웨이", "https://naver.me/51YaCiRG", "이탈리안BMT세트 9700원"),
            Restaurant("맘스터치", "https://naver.me/xhHPd1SZ", "싸이버거 7300원")
        )

        //일식 음식점 리스트
        val japaneseRestaurants = listOf(
            Restaurant("카레업자", "https://naver.me/56IHdH6u", "수제카츠카레 9900원"),
            Restaurant("가츠나베1.186", "https://naver.me/F9QphY6U", "김치가츠나베 10500원"),
            Restaurant("호랑이초밥", "https://naver.me/5D34KMSo", "호랑이모둠 13000원"),
            Restaurant("은행골", "https://naver.me/5Z0LLkfY", "특미초밥 12000원"),
            Restaurant("나미루라멘", "https://naver.me/xKEYQnXq", "돈코츠라멘 8500원"),
            Restaurant("코미오", "https://naver.me/xejkLuIw", "중화라멘 9900원"),
            Restaurant("히루방", "https://naver.me/F40ToKzw", "돈코츠라멘 9000원"),
            Restaurant("하코야", "https://naver.me/GeWBK0C6", "코이돈코츠 8000원"),
            Restaurant("온센", "https://naver.me/G8tVE0JY", "온센텐동 10900원")
        )

        // 기타 음식점 리스트
        val OtherFood = listOf(
            Restaurant("카레업자", "https://naver.me/56IHdH6u", "수제카츠카레 9900원"),
            Restaurant("포비엣콴", "https://naver.me/5WHwTCuh", "소고기쌀국수 10000원")
        )

        when(category){
            "한식" -> {
                val selectedRestaurant = KoreanFood.random()
                textView4.text = selectedRestaurant.name
                textView6.text = selectedRestaurant.menu
                button2.setOnClickListener {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(selectedRestaurant.url))
                    startActivity(intent)
                }
            }
            "중식" -> {
                val selectedRestaurant = ChineseFood.random()
                textView4.text = selectedRestaurant.name
                textView6.text = selectedRestaurant.menu
                button2.setOnClickListener {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(selectedRestaurant.url))
                    startActivity(intent)
                }
            }
            "양식" -> {
                val selectedRestaurant = WesternFood.random()
                textView4.text = selectedRestaurant.name
                textView6.text = selectedRestaurant.menu
                button2.setOnClickListener {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(selectedRestaurant.url))
                    startActivity(intent)
                }
            }
            "일식" -> {
                val selectedRestaurant = japaneseRestaurants.random()
                textView4.text = selectedRestaurant.name
                textView6.text = selectedRestaurant.menu
                button2.setOnClickListener {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(selectedRestaurant.url))
                    startActivity(intent)
                }
            }

            "기타" -> {
                val selectedRestaurant = OtherFood.random()
                textView4.text = selectedRestaurant.name
                textView6.text = selectedRestaurant.menu
                button2.setOnClickListener {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(selectedRestaurant.url))
                    startActivity(intent)
                }
            }
            else -> {
                println("카테고리 오류")
            }

        }

    }
}