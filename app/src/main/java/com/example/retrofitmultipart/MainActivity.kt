package com.example.retrofitmultipart

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import com.example.retrofitmultipart.api.ApiClint
import com.example.retrofitmultipart.api.ApiServise
import com.example.retrofitmultipart.model.ImageResponse
import com.example.retrofitmultipart.model.PathUtil
import com.github.dhaval2404.imagepicker.ImagePicker
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class MainActivity : AppCompatActivity() {
    private val PICK_IMAGE_CODE =1
    private lateinit var apiServise: ApiServise
    private var catPhoto: File = File("")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        apiServise = ApiClint.createServiceWithAuth(ApiServise::class.java)
        setupUI()
    }

    private fun setupUI() {
        val pickImage = findViewById<Button>(R.id.pick_image)
        val uploadImage = findViewById<Button>(R.id.upload_image)

        pickImage.setOnClickListener {
            checkPermission()
        }

        uploadImage.setOnClickListener {
            val builder: MultipartBody.Builder = MultipartBody.Builder()
            builder.setType(MultipartBody.FORM)

            if (catPhoto.length()>0){
                builder.addFormDataPart(
                    "file",
                    catPhoto.name,
                    catPhoto.asRequestBody("image/jpg".toMediaTypeOrNull())
                )
            }
            builder.addFormDataPart("sub_id","something")
            val body = builder.build()

            apiServise.uploadPhoto(body).enqueue(object :Callback<ImageResponse>{
                override fun onResponse(
                    call: Call<ImageResponse>,
                    response: Response<ImageResponse>
                ) {
                    Log.d("responseR", response.body()!!.sub_id)
                }

                override fun onFailure(call: Call<ImageResponse>, t: Throwable) {
                    Log.d("responseE", t.localizedMessage)
                }
            })
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode ==Activity.RESULT_OK && requestCode == PICK_IMAGE_CODE){
            var uri = data?.data?:return
            val path = PathUtil.getPath(this,uri)
            catPhoto = File(path)
        }
    }

    private fun checkPermission(){
        ImagePicker.with(this)
            .crop()
            .compress(1024)
            .start(PICK_IMAGE_CODE)
    }
}