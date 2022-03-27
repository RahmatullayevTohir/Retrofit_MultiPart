package com.example.retrofitmultipart.api

import com.example.retrofitmultipart.BuildConfig
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.util.concurrent.TimeUnit

object ApiClint {

    private const val BASE_URL = "https://api.thecatapi.com/v1/"

    private val client = buildClint()
    private val retrofit = buildRetrofit(client)

    private fun buildRetrofit(client: OkHttpClient):Retrofit{
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private fun buildClint():OkHttpClient{
        val interceptor = HttpLoggingInterceptor()
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
        val builder = OkHttpClient.Builder()
            .callTimeout(1, TimeUnit.MINUTES)
            .addNetworkInterceptor(object : Interceptor {
                @Throws(IOException::class)
                override fun intercept(chain: Interceptor.Chain): Response {
                    var request = chain.request()
                    val builder = request.newBuilder()
                    builder.addHeader("Accept", "application/json")
                    request = builder.build()
                    return chain.proceed(request)
                }
            })
        if (BuildConfig.DEBUG) {
            // Debug holatdan keyin o`chirish kerak!!!!!!!!!!!!!!!!!!!!!!!
            builder.addInterceptor(interceptor)
        }
        return builder.build()
    }

    fun <T> createServiceWithAuth(service: Class<T>):T{
        val newClient = client.newBuilder().addInterceptor(object :Interceptor{
            override fun intercept(chain: Interceptor.Chain): Response {
                var request = chain.request()
                val builder = request.newBuilder()
                builder.addHeader("x-api-key","6635fb35-de80-4d65-8cb8-be2383b63e5c")
                request = builder.build()
                return chain.proceed(request)
            }
        }).build()
        val newRetrofit = retrofit.newBuilder().client(newClient).build()
        return newRetrofit.create(service)
    }
}