package com.example.multipartapirequest

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.OpenableColumns
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.example.multipartapirequest.databinding.ActivityMainBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.io.FileOutputStream


class MainActivity : AppCompatActivity() {
    lateinit var mainBinding: ActivityMainBinding
    lateinit var fileUri: Uri

    private val contract = registerForActivityResult(ActivityResultContracts.GetContent()) {
        if (it != null) {
            fileUri = it
            mainBinding.ivFile.setImageDrawable(
                ContextCompat.getDrawable(
                    this,
                    R.drawable.pdf_icon
                )
            )
            mainBinding.tvFileTitle.text = getFileNameFromUri(fileUri)
            Log.d("Usman-Code", fileUri.toString())
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mainBinding.root)



        mainBinding.btnChange.setOnClickListener { contract.launch("application/pdf") }

        mainBinding.btnUpload.setOnClickListener { upload() }
    }

    private fun upload() {

        Log.d("Usman-Code", fileUri.toString())
        val fileDir = applicationContext.filesDir
        val file = File(fileDir, "mypdf.pdf")
        val inputStream = contentResolver.openInputStream(fileUri)
        val outputStream = FileOutputStream(file)
        inputStream!!.copyTo(outputStream)

        val part = MultipartBody.Part.createFormData(
            "file", file.name, RequestBody.create(
                MediaType.parse("application/pdf"), file
            )
        )
        val retrofit =
            Retrofit.Builder().baseUrl("https://api-eval.signnow.com")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(UploadService::class.java)



        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = retrofit.uploadDocument(part)
                if (response.id != null) {
                    Log.d("Usman-Code", response.id)
                    //show a toast
                    withContext(Dispatchers.Main) {
                        Toast.makeText(applicationContext, "File Uploaded", Toast.LENGTH_SHORT)
                            .show()
                    }
                }

            } catch (e: Exception) {
                Log.d("Usman-Code", e.toString())

            }
        }
    }

    private fun getFileNameFromUri(uri: Uri): String {
        val cursor = contentResolver.query(uri, null, null, null, null)
        var fileName = ""
        cursor?.use {
            if (it.moveToFirst()) {
                val displayNameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (displayNameIndex != -1) {
                    fileName = it.getString(displayNameIndex)
                }
            }
        }
        return fileName
    }
}