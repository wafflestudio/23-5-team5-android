package com.example.toyproject5.dto

import com.google.gson.annotations.SerializedName

data class LoginRequest(
    @SerializedName("username") val email: String, // backend로 보낼 때는 username
    @SerializedName("password") val password: String
)

data class LoginResponse(
    @SerializedName("access_token") val accessToken: String,
    @SerializedName("nickname") val nickname: String,
    @SerializedName("is_verified") val isVerified: Boolean
)

// 1. 인증 메일 발송 요청
data class EmailVerificationRequest(val email: String)

// 2. 인증 번호 확인 요청
data class EmailConfirmRequest(
    val email: String,
    val code: String
)

// 3. 일반 회원가입 요청
data class SignupRequest(
    val email: String,
    val password: String,
    val major: String,
    val student_number: String,
    val nickname: String
)

data class SocialLoginRequest(
    val token: String
)

data class SocialLoginResponse(
    val type: String,  // "REGISTER" 또는 "LOGIN" (또는 "SUCCESS")
    val token: String  // 임시 토큰 또는 최종 액세스 토큰
)

// 소셜 회원가입 요청
data class SocialSignupRequest(
    val registerToken: String,
    val email: String?,  // 스누메일이면 해당 이메일, 아니면 null
    val major: String,
    val student_number: String,
    val nickname: String
)

data class SignupResponse(
    val accessToken: String,
    val username: String,
    val nickname: String,
    val isVerified: Boolean
)

data class UserResponse(
    @SerializedName("accessToken") val accessToken: String,
    @SerializedName("nickname") val nickname: String,
    @SerializedName("is_verified") val isVerified: Boolean
)

data class UserMeResponse(
    @SerializedName("user_id") val userId: Long,
    val username: String,
    val major: String?,
    @SerializedName("student_number") val studentNumber: String,
    val nickname: String,
    @SerializedName("profile_image_url") val profileImageUrl: String?,
    val bio: String?,
    val role: String,
    @SerializedName("created_at") val createdAt: String
)

data class ImageResponse(
    @SerializedName("user_id") val userId: Int,
    @SerializedName("username") val username: String,
    @SerializedName("nickname") val nickname: String,
    @SerializedName("profile_image_url") val profileImageUrl: String?,
)