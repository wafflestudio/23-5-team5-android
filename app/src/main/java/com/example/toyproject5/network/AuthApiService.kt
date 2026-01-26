package com.example.toyproject5.network

import com.example.toyproject5.dto.GoogleLoginRequest
import com.example.toyproject5.dto.OAuthLoginResponse
import com.example.toyproject5.dto.LoginRequest
import com.example.toyproject5.dto.UserResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Path

interface AuthApiService {
    @POST("api/auth/login")
    suspend fun login(
        @Body loginRequest: LoginRequest
    ): Response<UserResponse>

    // 구글 로그인 API
    @POST("api/oauth/login/{provider}")
    suspend fun googleLogin(
        @Path("provider") provider: String = "google",
        @Body googleLoginRequest: GoogleLoginRequest
    ): Response<OAuthLoginResponse>
}