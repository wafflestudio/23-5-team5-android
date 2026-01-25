package com.example.toyproject5.dto

import com.google.gson.annotations.SerializedName

data class LoginRequest(
    @SerializedName("username") val email: String, // backend로 보낼 때는 username
    @SerializedName("password") val password: String
)

data class GoogleLoginRequest(
    @SerializedName("idToken") val idToken: String
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