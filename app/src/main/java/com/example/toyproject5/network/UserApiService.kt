package com.example.toyproject5.network

import com.example.toyproject5.dto.CursorResponse
import com.example.toyproject5.dto.ImageResponse
import com.example.toyproject5.dto.UserMeResponse
import com.example.toyproject5.dto.UserSearchResponseDto
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.*

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

    // 특정 그룹의 참여자 검색 (커서 기반 페이징)
    @GET("api/users/search")
    suspend fun searchUsersInGroup(
        @Query("groupId") groupId: Int,
        @Query("cursorId") cursorId: Long? = null,
        @Query("size") size: Int = 10
    ): Response<CursorResponse<UserSearchResponseDto>>
}