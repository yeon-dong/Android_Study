package com.autoever.carlist.activities

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.autoever.carlist.Car
import com.autoever.carlist.R
import com.autoever.carlist.api.RetrofitInstance
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {
    private lateinit var adapter: CarAdapter
    private val cars = mutableListOf<Car>()

    companion object {
        private const val REQUEST_CODE_ADD_LINE = 100
        private const val REQUEST_CODE_EDIT_LINE = 101
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val floatingActionButton = findViewById<FloatingActionButton>(R.id.floatingActionButton)
        floatingActionButton.setOnClickListener {
            val intent = Intent(applicationContext, AddCarActivity::class.java)
            startActivityForResult(intent, REQUEST_CODE_ADD_LINE) // 새 라인 추가
        }

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        adapter = CarAdapter(cars, this::onEditClicked, this::onDeleteClicked)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this) // 리니어 레이아웃 매니저: 리니어 하게 리스트 뿌려준다.

        fetch()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == RESULT_OK && (requestCode == REQUEST_CODE_ADD_LINE) || (requestCode == REQUEST_CODE_EDIT_LINE)) {
            fetch()
        }
    }

    fun onEditClicked(car: Car) {
        val intent = Intent(this, AddCarActivity::class.java).apply {
            putExtra("carId", car.id)
            putExtra("carContent", car.name)
        }
        startActivityForResult(intent, REQUEST_CODE_EDIT_LINE)
    }

    fun onDeleteClicked(car: Car) {
        lifecycleScope.launch {
            try {
                val response = RetrofitInstance.api.delete(car.id)
                if (response.isSuccessful) {
                    fetch()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun fetch() {
        lifecycleScope.launch {
            try {
                val response = RetrofitInstance.api.getList()
                if (response.isSuccessful && response.body() != null) {
                    withContext(Dispatchers.Main) {
                        cars.clear()
                        cars.addAll(response.body()!!)
                        adapter.notifyDataSetChanged()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}

class CarAdapter(
    private val cars: List<Car>,
    private val onEditClicked: (Car) -> Unit,
    private val onDeleteClicked: (Car) -> Unit
): RecyclerView.Adapter<CarAdapter.CarViewHolder>() {
    class CarViewHolder(view: View) :RecyclerView.ViewHolder(view) {
        val textView: TextView = view.findViewById(R.id.textView)
        val popup: ImageView = view.findViewById(R.id.popup)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CarViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_car, parent, false)
        return CarViewHolder(view)
    }

    override fun getItemCount(): Int {
        return cars.size
    }

    override fun onBindViewHolder(holder: CarViewHolder, position: Int) {
        val car = cars[position]
        holder.textView.text = car.name
        holder.popup.setOnClickListener {
            showPopupMenu(holder.popup, car)
        }
    }

    private fun showPopupMenu(view: View, car: Car) {
        val popupMenu = PopupMenu(view.context, view)
        popupMenu.inflate(R.menu.popup_menu) // 메뉴 리소스 연결

        // 메뉴 항목 클릭 리스너 설정
        popupMenu.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.action_edit -> {
                    // 수정 작업 처리
                    onEditClicked(car)
                    true
                }
                R.id.action_delete -> {
                    // 삭제 작업 처리
                    onDeleteClicked(car)
                    true
                }
                else -> false
            }
        }

        // 팝업 메뉴 표시
        popupMenu.show()
    }
}