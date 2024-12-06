package com.autoever.jamanchu.activities

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.autoever.jamanchu.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import java.io.File

class UploadActivity : AppCompatActivity() {
    private val REQUEST_CODE_CAMERA = 100
    private val REQUEST_CODE_GALLERY = 101
    private val CAMERA_PERMISSION_CODE = 102
    private var imageUri: Uri? = null
    private var photoUri: Uri? = null

    private lateinit var imageView: ImageView
    private lateinit var loadingOverlay: FrameLayout // 로딩 오버레이

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_upload)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // 로딩 오버레이
        loadingOverlay = findViewById(R.id.loadingOverlay)

        imageView = findViewById<ImageView>(R.id.imageView)
        val btnCamera = findViewById<TextView>(R.id.btnCamera)
        val btnAlbum = findViewById<TextView>(R.id.btnAlbum)
        val textViewUpload = findViewById<TextView>(R.id.textViewUpload)

        // 앨범 선택
        btnAlbum.setOnClickListener {
            openGallery()
        }

        // 카메라 선택
        btnCamera.setOnClickListener {
            checkCameraPermission()
        }

        // 업로드 선택
        findViewById<TextView>(R.id.textViewUpload).setOnClickListener {
            upload(imageUri!!)
        }
    }

    private fun showLoading(isLoading: Boolean) {
        loadingOverlay.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun upload(uri: Uri) {
        showLoading(true)

        val storage = FirebaseStorage.getInstance()
        val storageRef: StorageReference = storage.reference
        val imageRef = storageRef.child("images/${System.currentTimeMillis()}.jpg")
        val uploadTask: UploadTask = imageRef.putFile(uri)

        uploadTask.addOnSuccessListener { taskSnapshot ->
            imageRef.downloadUrl.addOnSuccessListener { downloadUrl ->
                imageUri = null
                showLoading(false)

                FirebaseAuth.getInstance().currentUser?.email?.let { email ->
                    updateUserImage(email, downloadUrl.toString())
                } ?: Toast.makeText(this, "사용자 인증 정보를 찾을 수 없습니다", Toast.LENGTH_SHORT).show()
            }
        }.addOnFailureListener {
            showLoading(false)
            Toast.makeText(this, "업로드 실패했습니다. 다시 시도해주세요.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateUserImage(email: String, imageUri: String) {
        val db = Firebase.firestore
        db.collection("users")
            .whereEqualTo("email", email)
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    for (document in documents) {
                        val userRef = db.collection("users").document(document.id)
                        userRef.update("image", imageUri)
                            .addOnSuccessListener {
                                val intent = Intent(this, MainActivity::class.java)
                                startActivity(intent)
                                finish()
                            }
                            .addOnFailureListener { exception ->
                                Toast.makeText(this, "이미지 업데이트에 실패했습니다.", Toast.LENGTH_SHORT).show()
                            }
                    }
                } else {
                    Toast.makeText(this, "해당 이메일의 사용자를 찾을 수 없습니다.", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "사용자 검색 중 오류가 발생했습니다.", Toast.LENGTH_SHORT).show()
            }
    }

    // 권한 요청 메서드 추가
    private fun checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.CAMERA), CAMERA_PERMISSION_CODE)
        } else {
            openCamera()
        }
    }

    private fun openCamera() {
        val photoFile = File(getExternalFilesDir(null), "photo_${System.currentTimeMillis()}.jpg")
        photoUri = FileProvider.getUriForFile(this, "${applicationContext.packageName}.provider", photoFile)

        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
        if (intent.resolveActivity(packageManager) != null) {
            startActivityForResult(intent, REQUEST_CODE_CAMERA)
        } else {
            Toast.makeText(this, "카메라를 사용할 수 없습니다", Toast.LENGTH_SHORT).show()
        }
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, REQUEST_CODE_GALLERY)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REQUEST_CODE_CAMERA -> {
                    imageView.setImageURI(photoUri)
                    imageUri = photoUri
                }
                REQUEST_CODE_GALLERY -> {
                    imageUri = data?.data
                    imageView.setImageURI(imageUri)
                }
            }
        }
    }
}