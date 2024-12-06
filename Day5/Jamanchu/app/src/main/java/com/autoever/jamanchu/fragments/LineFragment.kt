package com.autoever.jamanchu.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.autoever.jamanchu.R
import com.autoever.jamanchu.activities.LineActivity
import com.autoever.jamanchu.api.RetrofitInstance
import com.autoever.jamanchu.models.Line
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LineFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: LineAdapter
    private val lines = mutableListOf<Line>()
    private lateinit var floatingActionButton: FloatingActionButton
    private val firebaseAuth = FirebaseAuth.getInstance()

    companion object {
        private const val REQUEST_CODE_ADD_LINE = 100
        private const val REQUEST_CODE_EDIT_LINE = 101
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_line, container, false)

        floatingActionButton = view.findViewById(R.id.floatingActionButton)
        floatingActionButton.setOnClickListener {
            val intent = Intent(requireContext(), LineActivity::class.java)
            startActivityForResult(intent, REQUEST_CODE_ADD_LINE) // 새 라인 추가
        }

        // 리사이클러뷰
        recyclerView = view.findViewById(R.id.recyclerView)

        adapter = LineAdapter(lines, this::onEditClicked, this::onDeleteClicked)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(context) // 리니어 레이아웃 매니저: 리니어 하게 리스트 뿌려준다.

        fetchLines()
        return view
    }

    fun onEditClicked(line: Line) {
        if (line.user == firebaseAuth.currentUser?.uid) {
            val intent = Intent(requireContext(), LineActivity::class.java).apply {
                putExtra("lineId", line.id)
                putExtra("lineContent", line.line)
            }
            startActivityForResult(intent, REQUEST_CODE_EDIT_LINE)
        }
    }

    fun onDeleteClicked(line: Line) {
        if (line.user == firebaseAuth.currentUser?.uid) {
            lifecycleScope.launch {
                try {
                    val response = RetrofitInstance.api.deleteLine(line.id)
                    if (response.isSuccessful) {
                        fetchLines()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    fun fetchLines() {
        lifecycleScope.launch {
            try {
                val response = RetrofitInstance.api.getLines()
                if (response.isSuccessful && response.body() != null) {
                    withContext(Dispatchers.Main) {
                        lines.clear()
                        lines.addAll(response.body()!!)
                        adapter.notifyDataSetChanged()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == AppCompatActivity.RESULT_OK && (requestCode == REQUEST_CODE_ADD_LINE) || (requestCode == REQUEST_CODE_EDIT_LINE)) {
            fetchLines()
        }
    }
}

class LineAdapter(
    private val lines: List<Line>,
    private val onEditClicked: (Line) -> Unit,
    private val onDeleteClicked: (Line) -> Unit
) : RecyclerView.Adapter<LineAdapter.LineViewHolder>() {
    class LineViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textView: TextView = view.findViewById(R.id.textView)
        val buttonEdit: Button = view.findViewById(R.id.buttonEdit)
        val buttonDelete: Button = view.findViewById(R.id.buttonDelete)
        val popup: ImageView = view.findViewById(R.id.popup)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LineViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_line, parent, false)
        return LineViewHolder(view)
    }

    override fun getItemCount(): Int {
        return lines.size
    }

    override fun onBindViewHolder(holder: LineViewHolder, position: Int) {
        val line = lines[position]
        holder.textView.text = line.line

        val currentUserId = FirebaseAuth.getInstance().currentUser!!.uid

        holder.buttonEdit.visibility = if (line.user == currentUserId) View.VISIBLE else View.GONE
        holder.buttonEdit.setOnClickListener {
            onEditClicked(line)
        }

        holder.buttonDelete.visibility = if (line.user == currentUserId) View.VISIBLE else View.GONE
        holder.buttonDelete.setOnClickListener {
            onDeleteClicked(line)
        }

        holder.popup.setOnClickListener {
            showPopupMenu(holder.popup)
        }
    }

    private fun showPopupMenu(view: View) {
        val popupMenu = PopupMenu(view.context, view)
        popupMenu.inflate(R.menu.popup_menu) // 메뉴 리소스 연결

        // 메뉴 항목 클릭 리스너 설정
        popupMenu.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.action_edit -> {
                    // 수정 작업 처리
                    Toast.makeText(view.context, "수정 선택됨", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.action_delete -> {
                    // 삭제 작업 처리
                    Toast.makeText(view.context, "삭제 선택됨", Toast.LENGTH_SHORT).show()
                    true
                }
                else -> false
            }
        }

        // 팝업 메뉴 표시
        popupMenu.show()
    }
}








