package com.example.toyproject5.network

import com.example.toyproject5.dto.ImageResponse
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Multipart
import retrofit2.http.PUT
import retrofit2.http.Part

interface UserApiService {
    // 이미지 업로드를 위한 멀티파트 요청
    @Multipart
    @PUT("/api/users/me/profile-image")
    suspend fun uploadProfileImage(
        @Part profile_image: MultipartBody.Part
    ): Response<ImageResponse>
}