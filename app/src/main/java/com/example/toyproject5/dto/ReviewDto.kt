package com.example.toyproject5.dto

import com.google.gson.annotations.SerializedName

data class CreateReviewRequest(
    @SerializedName("groupId") val groupId: Int,
    @SerializedName("revieweeId") val revieweeId: Long,
    @SerializedName("rating") val rating: Int,
    @SerializedName("comment") val comment: String?
)

data class UpdateReviewRequest(
    @SerializedName("reviewId") val reviewId: Long,
    @SerializedName("rating") val rating: Int?,
    @SerializedName("comment") val comment: String?
)

data class DeleteReviewRequest(
    @SerializedName("reviewId") val reviewId: Long
)

data class ReviewResponse(
    @SerializedName("id") val id: Long,
    @SerializedName("groupId") val groupId: Int,
    @SerializedName("reviewerId") val reviewerId: Long,
    @SerializedName("reviewerNickname") val reviewerNickname: String?,
    @SerializedName("revieweeId") val revieweeId: Long,
    @SerializedName("revieweeNickname") val revieweeNickname: String?,
    @SerializedName("rating") val rating: Int,
    @SerializedName("comment") val comment: String?,
    @SerializedName("createdAt") val createdAt: String,
    @SerializedName("updatedAt") val updatedAt: String
)

data class PageResponse<T>(
    @SerializedName("content") val content: List<T>,
    @SerializedName("totalPages") val totalPages: Int,
    @SerializedName("totalElements") val totalElements: Long,
    @SerializedName("last") val last: Boolean,
    @SerializedName("first") val first: Boolean,
    @SerializedName("size") val size: Int,
    @SerializedName("number") val number: Int,
    @SerializedName("numberOfElements") val numberOfElements: Int,
    @SerializedName("empty") val empty: Boolean
)
