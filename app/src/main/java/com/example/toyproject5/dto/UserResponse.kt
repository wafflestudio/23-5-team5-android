package com.example.toyproject5.dto

import com.google.gson.annotations.SerializedName

data class LoginRequest(
    @SerializedName("username") val email: String, // backend로 보낼 때는 username
    @SerializedName("password") val password: String
)

data class UserResponse(
    @SerializedName("access_token") val accessToken: String,
    @SerializedName("nickname") val nickname: String,
    @SerializedName("is_verified") val isVerified: Boolean
)
data class ImageResponse(
    @SerializedName("user_id") val userId: Int,
    @SerializedName("username") val username: String,
    @SerializedName("nickname") val nickname: String,
    @SerializedName("profile_image_url") val profileImageUrl: String?,
)