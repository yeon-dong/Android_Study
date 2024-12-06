package com.autoever.jamanchu.activities

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.autoever.jamanchu.R
import com.autoever.jamanchu.models.Message
import com.autoever.jamanchu.models.User
import com.autoever.jamanchu.repositories.ChatRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ChatActivity : AppCompatActivity() {
    private lateinit var otherUser: String

    lateinit var textViewTitle: TextView
    lateinit var editText: EditText
    lateinit var imageButton: ImageButton
    private lateinit var recyclerView: RecyclerView

    var chatRoomId: String = ""

    private lateinit var chatRepository: ChatRepository

    private val messageList = mutableListOf<Message>()

    private lateinit var messageAdapter: MessageAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_chat)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setUI()

        // Intent로 전달된 상대 유저 id를 가져옴
        otherUser = intent.getStringExtra("otherUser") ?: ""
        chatRepository = ChatRepository()
        if (otherUser.isNotEmpty()) {
            // 채팅방 이름 설정
            setOtherUserInfo(otherUser) {
                textViewTitle.text = it?.nickname
            }

            // 참가자 목록을 설정
            val auth = FirebaseAuth.getInstance()
            val currentUser = auth.currentUser
            val participants = listOf(currentUser!!.uid, otherUser)

            // getChatRoom 호출
            chatRepository.getChatRoom(participants) { chatRoomId ->
                if (chatRoomId != null) {
                    // 채팅방 ID가 성공적으로 반환됨: 기존 채팅방 있음
                    println("Chat room ID: $chatRoomId")
                    this.chatRoomId = chatRoomId
                    // 해당 채팅방의 메세지 리스트를 가져온다.
                    setupMessageListener(chatRoomId) // 실시간 메시지 리스너 설정
                } else {
                    // 채팅방을 가져오는 데 실패함: 채팅방 없음 -> 여기선 아무것도 하지 않는다. 메세지를 보낼 때 체팅방을 생성한다.
                    println("Failed to get chat room")
                }
            }
        }
    }

    fun setOtherUserInfo(userId: String, callback: (User?) -> Unit) {
        // Firestore 인스턴스 가져오기
        val db = FirebaseFirestore.getInstance()

        // 사용자 ID를 사용하여 Firestore에서 사용자 데이터 가져오기
        db.collection("users") // "users"는 사용자 데이터가 저장된 컬렉션 이름입니다.
            .document(userId) // 사용자 ID를 문서 ID로 사용
            .get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    // 문서가 존재하는 경우 User 객체로 변환
                    val user = document.toObject(User::class.java)
                    callback(user) // 변환된 User 객체를 콜백으로 전달
                } else {
                    // 문서가 존재하지 않는 경우 null 전달
                    callback(null)
                }
            }
            .addOnFailureListener { exception ->
                // 오류 처리
                println("Error getting user document: $exception")
                callback(null) // 에러 발생 시 null 전달
            }
    }

    fun setUI() {
        textViewTitle = findViewById(R.id.textViewTitle)
        editText = findViewById(R.id.editText)
        imageButton = findViewById(R.id.imageButton)
        imageButton.setOnClickListener {
            if (editText.text.toString().isEmpty()) return@setOnClickListener

            val auth = FirebaseAuth.getInstance()
            val currentUser = auth.currentUser
            val participants = listOf(currentUser!!.uid, otherUser)

            if (chatRoomId.isEmpty()) {
                // 채팅방을 생성
                chatRepository.createChatRoom(participants) { newChatRoomId ->
                    if (newChatRoomId != null) {
                        chatRoomId = newChatRoomId
                        // 메시지를 보냄
                        val message = Message(
                            currentUser.uid,
                            editText.text.toString(),
                            System.currentTimeMillis()
                        )
                        chatRepository.sendMessage(newChatRoomId, message)
                        editText.setText("")

                        // 메시지 리스너 설정
                        setupMessageListener(newChatRoomId)
                    }
                }
            } else {
                // 기존 채팅방에 메시지 보냄
                val message = Message(
                    currentUser.uid,
                    editText.text.toString(),
                    System.currentTimeMillis()
                )
                chatRepository.sendMessage(chatRoomId, message)
                editText.setText("")
            }
        }

        // 리사이클러뷰 초기화
        recyclerView = findViewById(R.id.recyclerView)
        messageAdapter = MessageAdapter(messageList)
        recyclerView.adapter = messageAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)
    }

    private fun setupMessageListener(chatRoomId: String) {
        // Firestore에서 실시간 업데이트 리스너 설정
        val db = FirebaseFirestore.getInstance()
        db.collection("chatRooms") // 채팅방 컬렉션 이름
            .document(chatRoomId)
            .collection("messages") // 메시지가 저장된 서브 컬렉션 이름
            .addSnapshotListener { snapshots, e ->
                if (e != null) {
                    println("Listen failed: $e")
                    return@addSnapshotListener
                }

                if (snapshots != null) {
                    // 새로운 메시지 목록
                    val newMessages = mutableListOf<Message>()
                    for (doc in snapshots.documentChanges) {
                        if (doc.type == DocumentChange.Type.ADDED) {
                            val message = doc.document.toObject(Message::class.java)
                            newMessages.add(message)
                        }
                    }

                    // 기존 메시지 목록에 새 메시지를 추가
                    messageList.addAll(newMessages)

                    // 메시지 목록을 타임스탬프 기준으로 정렬
                    messageList.sortBy { it.timestamp }

                    // 어댑터에 데이터 변경 알리기
                    messageAdapter.notifyDataSetChanged()

                    // 최신 메시지로 스크롤
                    if (newMessages.isNotEmpty()) {
                        recyclerView.scrollToPosition(messageList.size - 1)
                    }
                }
            }
    }
}

class MessageAdapter(
    private val messages: List<Message>
) : RecyclerView.Adapter<MessageAdapter.MessageViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_message, parent, false)
        return MessageViewHolder(view)
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        val message = messages[position]
        holder.bind(message)
    }

    override fun getItemCount(): Int = messages.size

    class MessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val layout: LinearLayout = itemView.findViewById(R.id.layout)

        private val layoutReceivedMessage: LinearLayout = itemView.findViewById(R.id.layoutReceivedMessage)
        private val textViewReceivedMessageContent: TextView = itemView.findViewById(R.id.textViewReceivedMessageContent)
        private val textViewReceivedMessageTimestamp: TextView = itemView.findViewById(R.id.textViewReceivedMessageTimestamp)

        private val layoutSentMessage: LinearLayout = itemView.findViewById(R.id.layoutSentMessage)
        private val textViewSentMessageContent: TextView = itemView.findViewById(R.id.textViewSentMessageContent)
        private val textViewSentMessageTimestamp: TextView = itemView.findViewById(R.id.textViewSentMessageTimestamp)

        fun bind(message: Message) {
            val currentUserUid = FirebaseAuth.getInstance().currentUser?.uid

            if (message.senderId == currentUserUid) {
                // 송신자의 메시지일 경우
                layout.gravity = Gravity.END

                layoutSentMessage.visibility = View.VISIBLE
                layoutReceivedMessage.visibility = View.GONE
                textViewSentMessageContent.text = message.text

                val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
                textViewSentMessageTimestamp.text = sdf.format(Date(message.timestamp))
            } else {
                // 수신자의 메시지일 경우
                layout.gravity = Gravity.START

                layoutSentMessage.visibility = View.GONE
                layoutReceivedMessage.visibility = View.VISIBLE
                textViewReceivedMessageContent.text = message.text

                val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
                textViewReceivedMessageTimestamp.text = sdf.format(Date(message.timestamp))
            }
        }
    }
}
