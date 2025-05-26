package com.example.ziplinemobile.network

import retrofit2.Call
import retrofit2.http.*
import okhttp3.MultipartBody

interface ZiplineApi {
    @GET("files")
    fun getFiles(): Call<List<FileItem>>

    @Multipart
    @POST("upload")
    fun uploadFile(@Part file: MultipartBody.Part): Call<UploadResponse>

}

data class FileItem(val name: String, val url: String)
data class UploadResponse(val success: Boolean, val message: String)