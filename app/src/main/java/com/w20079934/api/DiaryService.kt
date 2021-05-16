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
    fun getall(): Call<MutableList<EntryModel>>

    @GET("/entries/{email}")
    fun get(@Path("email") id: String): Call<EntryModel>

    @DELETE("/entries/{email}/{id}")
    fun delete(@Path("id") id: String,
                @Path("email") email: String): Call<EntryModel>

    @POST("/entries/{email}")
    fun post(@Path("email") email: String, @Body donation: EntryModel): Call<EntryWrapper>

    @PUT("/entries/{email}/{id}")
    fun put(@Path("id") id: String, @Path("email") email: String,
            @Body donation: EntryModel
    ): Call<MutableList<EntryModel>>

    companion object {

        val serviceURL = "127.0.0.1"

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