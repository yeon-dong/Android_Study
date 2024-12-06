package com.autoever.jamanchu.activities

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.autoever.jamanchu.R
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class FriendActivity : AppCompatActivity() {
    lateinit var adapter: FriendAdapter
    private var friends: MutableList<Pair<String, String>> = mutableListOf() // 친구 (ID, 이름) 리스트

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_friend)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        adapter = FriendAdapter(friends, this::onRemoveClicked)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        loadFriends()
    }

    fun onRemoveClicked(context: Context, friendId: String) {
        val auth = FirebaseAuth.getInstance()
        val currentUserId = auth.currentUser!!.uid
        val userRef = FirebaseFirestore.getInstance()
            .collection("users").document(currentUserId)
        userRef.get().addOnSuccessListener { documentSnapshot ->
            val currentFriends = documentSnapshot.get("friends")
                    as? List<String> ?: emptyList()

            // 친구 ID가 존재하는 경우에만 삭제
            if (currentFriends.contains(friendId)) {
                val updatedFriends = currentFriends - friendId // 친구 삭제

                userRef.update("friends", updatedFriends)
                    .addOnSuccessListener {
                        Log.d("Firestore", "Friend removed successfully")
                        Toast.makeText(context, "친구가 삭제되었습니다.", Toast.LENGTH_SHORT).show()

                        loadFriends()
                    }
                    .addOnFailureListener { e ->
                        Log.w("Firestore", "Error removing friend", e)
                    }
            } else {
                Toast.makeText(context, "이미 친구가 아닌 사용자입니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun loadFriends() {
        val auth = FirebaseAuth.getInstance()
        val currentUserId = auth.currentUser!!.uid
        val userRef = FirebaseFirestore.getInstance().collection("users").document(currentUserId)

        userRef.get().addOnSuccessListener { document ->
            if (document != null) {
                val friendIds = document.get("friends") as? List<String> ?: emptyList()
                fetchFriendNames(friendIds) // 친구 닉네임 가져오기
            }
        }.addOnFailureListener { e ->
            Log.w("FriendActivity", "Error getting friends", e)
        }
    }

    fun fetchFriendNames(friendsIds: List<String>) {
        val friendNames = mutableListOf<Pair<String, String>>() // (friendId, friendName) 쌍 리스트
        val db = FirebaseFirestore.getInstance()

        // 친구 ID 리스트를 순회하여 친구 닉네임을 가져옴
        val fetchTasks = friendsIds.map { friendId ->
            db.collection("users").document(friendId).get()
                .addOnSuccessListener { friendDocument ->
                    if (friendDocument != null) {
                        val friendName = friendDocument.getString("nickname") ?: "Unknown"
                        friendNames.add(Pair(friendId, friendName)) // (friendId, friendName) 추가
                    }
                }
                .addOnFailureListener { e ->
                    Log.w("Firestore", "Error getting friend name for ID: $friendId", e)
                }
        }

        // 모든 친구 닉네임을 가져온 후 RecyclerView 업데이트
        Tasks.whenAllComplete(fetchTasks).addOnCompleteListener {
            friends.clear()
            friends.addAll(friendNames)
            adapter.notifyDataSetChanged() // 데이터 변경 알림
        }
    }
}

class FriendAdapter(
    private var friends: List<Pair<String, String>>, // (friendId, friendName)
    private var onRemoveClicked: (Context, String) -> Unit,
) : RecyclerView.Adapter<FriendAdapter.FriendViewHolder>() {
    class FriendViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textView = view.findViewById<TextView>(R.id.textView)
        val button = view.findViewById<Button>(R.id.button)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FriendViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_friend, parent, false)
        return FriendViewHolder(view)
    }

    override fun getItemCount(): Int {
        return friends.size
    }

    override fun onBindViewHolder(holder: FriendViewHolder, position: Int) {
        val friend = friends[position]
        val (friendId, friendName) = friend
        holder.textView.text = friendName
        holder.button.setOnClickListener {
            onRemoveClicked(holder.itemView.context, friendId)
        }
    }
}