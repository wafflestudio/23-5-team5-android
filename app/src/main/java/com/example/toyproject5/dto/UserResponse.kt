package com.example.toyproject5.dto

import com.google.gson.annotations.SerializedName

data class LoginRequest(
    @SerializedName("username") val email: String, // backend로 보낼 때는 username
    @SerializedName("password") val password: String
)

data class GoogleLoginRequest(
    val token: String
)

data class OAuthLoginResponse(
    val type: String,  // "REGISTER" 또는 "LOGIN" (또는 "SUCCESS")
    val token: String  // 임시 토큰 또는 최종 액세스 토큰
)

data class UserResponse(
    @SerializedName("accessToken") val accessToken: String,
    @SerializedName("nickname") val nickname: String,
    @SerializedName("isVerified") val isVerified: Boolean
)
data class ImageResponse(
    @SerializedName("status") val status: String,
    @SerializedName("message") val message: String,
    @SerializedName("imageUrl") val imageUrl: String // todo:서버에 저장된 진짜 인터넷 주소
)