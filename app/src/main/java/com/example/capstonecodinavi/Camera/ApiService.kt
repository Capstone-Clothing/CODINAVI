package com.example.capstonecodinavi.Camera

import retrofit2.Call
import okhttp3.MultipartBody
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface ApiService {
    @Multipart
    @POST("upload")
    fun uploadImage(
        @Part file: MultipartBody.Part
    ): Call<String>
}