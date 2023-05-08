package com.example.multipartapirequest

import retrofit2.http.POST
import retrofit2.http.Multipart
import retrofit2.http.Part
import okhttp3.MultipartBody
import okhttp3.Response
import okhttp3.ResponseBody
import retrofit2.http.Headers

interface UploadService {


    @Headers("Authorization:Bearer 5ba7cddd3d0d2c27a1bfa0b63e516d9ccb365799307546c2af9e8dcd1916faf4")
    @Multipart
    @POST("/document")
    suspend fun uploadDocument(
        @Part file:MultipartBody.Part
    ) : SignupResponse

}