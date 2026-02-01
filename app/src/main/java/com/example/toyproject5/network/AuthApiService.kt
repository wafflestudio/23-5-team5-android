package com.example.toyproject5.network

import com.example.toyproject5.dto.SocialLoginRequest
import com.example.toyproject5.dto.SocialLoginResponse
import com.example.toyproject5.dto.LoginRequest
import com.example.toyproject5.dto.UserResponse
import com.example.toyproject5.dto.SocialSignupRequest
import com.example.toyproject5.dto.SocialSignupResponse
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
        @Body googleLoginRequest: SocialLoginRequest
    ): Response<SocialLoginResponse>

    // 구글 회원가입 API
    @POST("api/oauth/signUp/{provider}")
    suspend fun googleSignup(
        @Path("provider") provider: String = "google",
        @Body request: SocialSignupRequest
    ): Response<SocialSignupResponse>
}