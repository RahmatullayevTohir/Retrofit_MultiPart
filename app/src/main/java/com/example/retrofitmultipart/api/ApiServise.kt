package com.example.retrofitmultipart.api

import com.example.retrofitmultipart.model.ImageResponse
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiServise {

    @POST("images/upload")
    fun uploadPhoto(@Body body:RequestBody):Call<ImageResponse>
}