package com.example.toyproject5.network

import com.example.toyproject5.dto.ImageResponse
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface UserApiService {

    // 이미지 업로드를 위한 멀티파트 요청
    @Multipart
    @POST("user/profile-image") // TODO: 추후 실제 서버 주소로 변경
    suspend fun uploadProfileImage(
        @Part image: MultipartBody.Part
    ): Response<ImageResponse>
}