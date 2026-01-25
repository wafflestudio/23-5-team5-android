package com.example.toyproject5.network

import com.example.toyproject5.dto.GoogleLoginRequest
import com.example.toyproject5.dto.LoginRequest
import com.example.toyproject5.dto.UserResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApiService {
    @POST("api/auth/login")
    suspend fun login(
        @Body loginRequest: LoginRequest
    ): Response<UserResponse>

    // 구글 로그인 API 추가
    @POST("api/v1/auth/google")
    suspend fun googleLogin(
        @Body googleLoginRequest: GoogleLoginRequest
    ): Response<UserResponse>
}