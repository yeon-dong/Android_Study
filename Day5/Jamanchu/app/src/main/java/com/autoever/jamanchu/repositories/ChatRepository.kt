package com.autoever.jamanchu.repositories

import com.autoever.jamanchu.models.ChatRoom
import com.autoever.jamanchu.models.Message
import com.google.firebase.firestore.FirebaseFirestore

class ChatRepository {
    private val db = FirebaseFirestore.getInstance()

    // 채팅방 생성
    fun createChatRoom(participants: List<String>, callback: (String?) -> Unit) {
        val sortedParticipants = participants.sorted()
        val chatRoom = ChatRoom(participants = sortedParticipants)
        db.collection("chatRooms").add(chatRoom)
            .addOnSuccessListener { documentReference ->
                callback(documentReference.id) // 생성된 채팅방 ID 반환
            }
            .addOnFailureListener { exception ->
                println("Error creating chat room: $exception")
                callback(null)
            }
    }

    // 메시지 전송
    fun sendMessage(chatRoomId: String, message: Message) {
        db.collection("chatRooms").document(chatRoomId)
            .collection("messages").add(message)
            .addOnSuccessListener {
                // 메시지 전송 성공 시, 채팅방의 마지막 메시지 업데이트
                updateLastMessage(chatRoomId, message)
            }
            .addOnFailureListener { exception ->
                println("Error sending message: $exception")
            }
    }

    // 채팅방의 마지막 메시지 업데이트
    private fun updateLastMessage(chatRoomId: String, message: Message) {
        // MutableMap<String, Any>으로 선언하여 타입 불일치 문제를 방지
        val chatRoomUpdate = mutableMapOf<String, Any>(
            "lastMessage" to message.text,
            "lastMessageTimestamp" to message.timestamp
        )

        db.collection("chatRooms").document(chatRoomId)
            .update(chatRoomUpdate) // 이제 이 줄에서 타입 불일치 문제가 발생하지 않음
            .addOnFailureListener { exception ->
                println("Error updating last message: $exception")
            }
    }

    // 채팅방 가져오기
    fun getChatRoom(participants: List<String>, callback: (String?) -> Unit) {
        // 참가자 목록을 정렬하여 같은 순서로 만듭니다.
        val sortedParticipants = participants.sorted()
        // 1. 기존에 해당 유저들로 구성된 채팅방이 있는지 확인
        db.collection("chatRooms")
            .whereEqualTo("participants", sortedParticipants)
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    // 기존 채팅방이 존재할 경우
                    val existingChatRoomId = documents.documents[0].id
                    callback(existingChatRoomId) // 기존 채팅방 ID 반환
                } else {
                    callback(null)
                }
            }
            .addOnFailureListener { exception ->
                println("Error checking existing chat room: $exception")
                callback(null) // 에러 발생 시 null 반환
            }
    }
}