package com.w20079934.api

import com.google.gson.GsonBuilder
import com.w20079934.models.EntryModel
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import java.util.concurrent.TimeUnit

interface DiaryService {
    @GET("/entries")
    fun getall(): Call<List<EntryModel>>

    @GET("/entries/{id}")
    fun get(@Path("id") id: String): Call<EntryModel>

    @DELETE("/entries/{id}")
    fun delete(@Path("id") id: String): Call<EntryModel>

    @POST("/entries")
    fun post(@Body donation: EntryModel): Call<EntryWrapper>

    @PUT("/entries/{id}")
    fun put(@Path("id") id: String,
            @Body donation: EntryModel
    ): Call<EntryWrapper>

    companion object {

        val serviceURL = ""

        fun create() : DiaryService {

            val gson = GsonBuilder().create()

            val okHttpClient = OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build()

            val retrofit = Retrofit.Builder()
                .baseUrl(serviceURL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(okHttpClient)
                .build()
            return retrofit.create(DiaryService::class.java)
        }
    }
}