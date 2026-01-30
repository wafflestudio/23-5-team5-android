package com.example.toyproject5.dto

import com.google.gson.annotations.SerializedName

/**
 * Request DTO for creating a group
 */
data class GroupCreateRequest(
    @SerializedName("group_name") val groupName: String,
    @SerializedName("description") val description: String,
    @SerializedName("category_id") val categoryId: Int,
    @SerializedName("sub_category_id") val subCategoryId: Int,
    @SerializedName("capacity") val capacity: Int?,
    @SerializedName("is_online") val isOnline: Boolean,
    @SerializedName("location") val location: String
)

/**
 * Request DTO for group operations using group_id
 */
data class GroupIdRequest(
    @SerializedName("group_id") val groupId: Int
)

/**
 * Request DTO for joining/withdrawing from a group
 */
data class GroupJoinRequest(
    @SerializedName("groupId") val groupId: Int
)

/**
 * Response DTO for group search results
 */
data class GroupResponse(
    @SerializedName("id") val id: Int,
    @SerializedName("group_name") val groupName: String,
    @SerializedName("description") val description: String,
    @SerializedName("category_id") val categoryId: Int,
    @SerializedName("sub_category_id") val subCategoryId: Int,
    @SerializedName("capacity") val capacity: Int?,
    @SerializedName("leader_id") val leaderId: Int,
    @SerializedName("is_online") val isOnline: Boolean,
    @SerializedName("location") val location: String,
    @SerializedName("status") val status: String,
    @SerializedName("created_at") val createdAt: String?
)

/**
 * Wrapper for paginated group search results
 */
data class GroupSearchResponse(
    @SerializedName("content") val content: List<GroupResponse>
)
