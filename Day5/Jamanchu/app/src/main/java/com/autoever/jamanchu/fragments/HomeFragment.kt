package com.autoever.jamanchu.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat.startActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.datingapp.R
import com.example.datingapp.activities.ChatActivity
import com.example.datingapp.activities.MainActivity
import com.example.datingapp.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class HomeFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: MyAdapter
    val users: MutableList<User> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        recyclerView = view.findViewById(R.id.recyclerView)

        // 그리드 레이아웃 매니저 설정
        val layoutManager = GridLayoutManager(requireContext(), 2) // 열 개수 설정
        recyclerView.layoutManager = layoutManager

        // 데이터 설정
        adapter = MyAdapter(users)
        recyclerView.adapter = adapter

        fetchUsers()

        return view
    }

    fun fetchUsers() {
        users.clear()
        // Firestore 인스턴스 가져오기
        val db = Firebase.firestore
        val auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser
        val currentUserId = currentUser!!.uid // 현재 사용자 ID 가져오기

        db.collection("users") // "users"는 저장된 사용자 데이터의 컬렉션 이름입니다.
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    // 데이터 변환, 문서 ID도 포함
                    val user = document.toObject(User::class.java).copy(id = document.id)
                    // 필요한 작업 수행 (예: 로그 출력, UI 업데이트 등)
                    println("User: ${user.nickname}, Email: ${user.email}, Gender: ${user.gender}, Age: ${user.age}")

                    // 현재 사용자 제외 (ID 비교)
                    if (currentUserId != user.id) {
                        users.add(user)
                    }
                }
                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                // 오류 처리
                println("Error getting documents: $exception")
            }
    }
}

class MyAdapter(private val users: List<User>) : RecyclerView.Adapter<MyAdapter.ViewHolder>() {
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textView: TextView = itemView.findViewById(R.id.textView)
        val imageView: ImageView = itemView.findViewById(R.id.imageView)
        val buttonFriend: TextView = itemView.findViewById(R.id.buttonFriend)
        val buttonChat: TextView = itemView.findViewById(R.id.buttonChat)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_home, parent, false) // 아이템 레이아웃 추가
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val user = users[position]
        holder.textView.text = user.nickname // 아이템 데이터 설정
        holder.imageView.setImageResource(R.drawable.idol1)
        Glide.with(holder.itemView.context)
            .load(user.image) // 불러올 이미지의 URL 또는 URI
            .placeholder(R.drawable.user)
            .into(holder.imageView) // 이미지를 표시할 ImageView

        // 친구추가
        holder.buttonFriend.setOnClickListener {
            val auth = FirebaseAuth.getInstance()
            val currentUser = auth.currentUser
            val currentUserId = currentUser!!.uid // 현재 사용자 ID 가져오기
            addFriend(holder.itemView.context, currentUserId, user.id)
        }

        // 채팅하기
        holder.buttonChat.setOnClickListener {
            val context = holder.itemView.context // Context 가져오기
            val intent = Intent(context, ChatActivity::class.java)
            // 채팅방 ID 전달
            intent.putExtra("otherUser", user.id)
            context.startActivity(intent) // Context를 사용해 startActivity 호출
        }
    }

    override fun getItemCount() = users.size

    fun addFriend(context: Context, currentUserId: String, friendId: String) {
        val userRef = FirebaseFirestore.getInstance().collection("users").document(currentUserId)

        // Firestore 트랜잭션을 사용하여 친구 리스트를 안전하게 업데이트
        FirebaseFirestore.getInstance().runTransaction { transaction ->
            val userSnapshot = transaction.get(userRef)
            val currentFriends = userSnapshot.get("friends") as? List<String> ?: emptyList()

            // 친구 ID가 이미 존재하지 않는 경우에만 추가
            if (!currentFriends.contains(friendId)) {
                val updatedFriends = currentFriends + friendId // 새로운 친구 ID 추가
                transaction.update(userRef, "friends", updatedFriends)
            }
        }.addOnSuccessListener {
            // 성공적으로 친구 추가됨
            Log.d("Firestore", "Friend added successfully")
            Toast.makeText(context, "친구가 추가되었습니다.", Toast.LENGTH_SHORT).show()
        }.addOnFailureListener { e ->
            // 친구 추가 실패
            Log.w("Firestore", "Error adding friend", e)
        }
    }
}