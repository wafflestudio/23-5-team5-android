package com.example.toyproject5.network

import com.example.toyproject5.dto.ImageResponse
import com.example.toyproject5.dto.UserMeResponse
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.PATCH
import retrofit2.http.PUT
import retrofit2.http.Part

interface UserApiService {
    // 이미지 업로드를 위한 멀티파트 요청
    @Multipart
    @PUT("/api/users/me/profile-image")
    suspend fun uploadProfileImage(
        @Part profile_image: MultipartBody.Part
    ): Response<ImageResponse>

    // 내 정보 가져오기 (자동 로그인 확인용)
    @GET("api/users/me")
    suspend fun getUserMe(): Response<UserMeResponse>

    // 내 프로필 수정 (PATCH)
    @Multipart
    @PATCH("/api/users/me")
    suspend fun updateProfileText(
        @Part nickname: MultipartBody.Part? = null,
        @Part major: MultipartBody.Part? = null,
        @Part bio: MultipartBody.Part? = null
    ): Response<UserMeResponse>
}