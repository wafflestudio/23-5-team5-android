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

data class UserMeResponse(
    @SerializedName("user_id") val userId: Long,
    val username: String,
    val major: String,
    @SerializedName("student_number") val studentNumber: String,
    val nickname: String,
    @SerializedName("profile_image_url") val profileImageUrl: String?,
    val bio: String?,
    val role: String,
    @SerializedName("created_at") val createdAt: String
)

data class UserSearchResponseDto(
    @SerializedName("userId") val userId: Long,
    val username: String,
    val nickname: String,
    val major: String,
    @SerializedName("studentNumber") val studentNumber: String,
    @SerializedName("profileImageUrl") val profileImageUrl: String?,
    val bio: String?,
    val role: String,
    @SerializedName("createdAt") val createdAt: String
)

data class ImageResponse(
    @SerializedName("user_id") val userId: Int,
    @SerializedName("username") val username: String,
    @SerializedName("nickname") val nickname: String,
    @SerializedName("profile_image_url") val profileImageUrl: String?,
)

data class CursorResponse<T>(
    @SerializedName("content") val content: List<T>,
    @SerializedName("nextCursorId") val nextCursorId: Long?,
    @SerializedName("hasNext") val hasNext: Boolean
)
