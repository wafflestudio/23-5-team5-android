package com.example.toyproject5.network

import com.example.toyproject5.dto.EmailConfirmRequest
import com.example.toyproject5.dto.EmailVerificationRequest
import com.example.toyproject5.dto.SocialLoginRequest
import com.example.toyproject5.dto.SocialLoginResponse
import com.example.toyproject5.dto.LoginRequest
import com.example.toyproject5.dto.LoginResponse
import com.example.toyproject5.dto.SignupRequest
import com.example.toyproject5.dto.SignupResponse
import com.example.toyproject5.dto.SocialSignupRequest
import com.example.toyproject5.dto.SocialVerifyRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Path

interface AuthApiService {
    // 일반 로그인
    @POST("api/auth/login")
    suspend fun login(
        @Body loginRequest: LoginRequest
    ): Response<LoginResponse>

    // 인증번호 발송
    @POST("api/auth/code")
    suspend fun sendVerificationEmail(
        @Body request: EmailVerificationRequest
    ): Response<Unit>

    // 인증번호 확인
    @POST("api/auth/verify")
    suspend fun verifyEmailCode(
        @Body request: EmailConfirmRequest
    ): Response<Unit>

    // 일반 회원가입
    @POST("api/auth/signup")
    suspend fun signup(
        @Body request: SignupRequest
    ): Response<SignupResponse>

    // 구글 로그인 API
    @POST("api/oauth/login/{provider}")
    suspend fun googleLogin(
        @Path("provider") provider: String = "google",
        @Body googleLoginRequest: SocialLoginRequest
    ): Response<SocialLoginResponse>

    // 소셜 인증번호 확인
    @POST("api/auth/social/verify") // 예시: 코드 확인
    suspend fun verifySocialEmailCode(
        @Body request: SocialVerifyRequest
    ): Response<SocialLoginResponse>

    // 구글 회원가입 API
    @POST("api/oauth/signUp/{provider}")
    suspend fun googleSignup(
        @Path("provider") provider: String = "google",
        @Body request: SocialSignupRequest
    ): Response<SignupResponse>
}