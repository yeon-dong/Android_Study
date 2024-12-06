package com.autoever.jamanchu.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.autoever.jamanchu.R
import com.autoever.jamanchu.activities.ChatActivity
import com.autoever.jamanchu.models.ChatRoom
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ChatFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ChatRoomAdapter
    private val chatRooms = mutableListOf<ChatRoom>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_chat, container, false)
        recyclerView = view.findViewById(R.id.recyclerView)
        adapter = ChatRoomAdapter(chatRooms)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(context)

        fetchChatRooms() // 채팅방 리스트 불러오기

        return view
    }

    private fun fetchChatRooms() {
        val myId = FirebaseAuth.getInstance().currentUser?.uid
        if (myId != null) {
            FirebaseFirestore.getInstance().collection("chatRooms")
                .whereArrayContains("participants", myId) // participants에 현재 사용자 ID가 포함된 채팅방만 가져오기
                .get()
                .addOnSuccessListener { documents ->
                    chatRooms.clear() // 기존 데이터를 지우고 새로 추가
                    for (document in documents) {
                        val chatRoom = document.toObject(ChatRoom::class.java)
                        chatRooms.add(chatRoom)
                    }
                    adapter.notifyDataSetChanged() // 데이터 변경 알리기
                }
                .addOnFailureListener { exception ->
                    println("Error getting chat rooms: $exception")
                }
        }
    }
}

class ChatRoomAdapter(private val chatRooms: List<ChatRoom>) : RecyclerView.Adapter<ChatRoomAdapter.ChatRoomViewHolder>() {

    inner class ChatRoomViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textViewRoomName: TextView = itemView.findViewById(R.id.textViewRoomName)
        val textViewLastMessage: TextView = itemView.findViewById(R.id.textViewLastMessage)
        val textViewTimestamp: TextView = itemView.findViewById(R.id.textViewTimestamp)

        init {
            itemView.setOnClickListener {
                // 클릭 시 액티비티로 이동
                val position = bindingAdapterPosition // 현재 아이템의 위치
                if (position != RecyclerView.NO_POSITION) {
                    val chatRoom = chatRooms[position]
                    val context = itemView.context

                    // 상대방 ID 가져오기
                    val myId = FirebaseAuth.getInstance().currentUser?.uid
                    val participants = chatRoom.participants
                    val otherParticipants = participants.filter { it != myId }

                    if (otherParticipants.isNotEmpty()) {
                        val firstOpponentId = otherParticipants.first()

                        // Intent 생성 및 상대방 ID 전달
                        val intent = Intent(context, ChatActivity::class.java).apply {
                            putExtra("otherUser", firstOpponentId) // 상대방 ID를 전달
                        }
                        context.startActivity(intent) // Context를 사용해 startActivity 호출
                    }
                }
            }
        }

        fun bind(chatRoom: ChatRoom) {
            // 상대방 닉네임 가져오기
            val myId = FirebaseAuth.getInstance().currentUser?.uid
            val participants = chatRoom.participants
            val otherParticipants = participants.filter { it != myId }
            if (otherParticipants.isNotEmpty()) {
                val firstOpponentId = otherParticipants.first()
                val db = FirebaseFirestore.getInstance()
                db.collection("users").document(firstOpponentId)
                    .get()
                    .addOnSuccessListener { document ->
                        if (document != null) {
                            // 닉네임 필드를 가져옴
                            val nickname = document.getString("nickname")
                            if (nickname != null) {
                                // 닉네임 사용
                                Log.d("Chat", "상대방 닉네임: $nickname")
                                textViewRoomName.text = nickname
                            } else {
                                Log.d("Chat", "닉네임이 존재하지 않습니다.")
                            }
                        } else {
                            Log.d("Chat", "해당 문서를 찾을 수 없습니다.")
                        }
                    }
                    .addOnFailureListener { exception ->
                        Log.w("Chat", "닉네임 가져오기 실패: ", exception)
                    }
            }
            textViewLastMessage.text = chatRoom.lastMessage
            // 타임스탬프 포맷팅 처리
            textViewTimestamp.text = formatTimestamp(chatRoom.lastMessageTimestamp)
        }

        private fun formatTimestamp(timestamp: Long): String {
            val dateFormat = SimpleDateFormat("MM.dd HH:mm", Locale.getDefault())
            val date = Date(timestamp)
            return dateFormat.format(date)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatRoomViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_chat_room, parent, false)
        return ChatRoomViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChatRoomViewHolder, position: Int) {
        holder.bind(chatRooms[position])
    }

    override fun getItemCount(): Int = chatRooms.size
}
