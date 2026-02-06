package com.example.toyproject5.network

import com.example.toyproject5.dto.GroupCreateRequest
import com.example.toyproject5.dto.GroupIdRequest
import com.example.toyproject5.dto.GroupJoinRequest
import com.example.toyproject5.dto.GroupResponse
import com.example.toyproject5.dto.GroupSearchResponse
import retrofit2.Response
import retrofit2.http.*

interface GroupApiService {

    // 6. Group Management

    @POST("api/groups")
    suspend fun createGroup(
        @Body request: GroupCreateRequest
    ): Response<Unit>

    @HTTP(method = "DELETE", path = "api/groups", hasBody = true)
    suspend fun deleteGroup(
        @Body request: GroupIdRequest
    ): Response<Unit>

    @PATCH("api/groups/expire")
    suspend fun expireGroup(
        @Body request: GroupIdRequest
    ): Response<Unit>

    // 7. User-Group Operations

    @POST("api/groups/join")
    suspend fun joinGroup(
        @Body request: GroupJoinRequest
    ): Response<Unit>

    @HTTP(method = "DELETE", path = "api/groups/join", hasBody = true)
    suspend fun withdrawFromGroup(
        @Body request: GroupJoinRequest
    ): Response<Unit>

    // 8. Search

    @GET("api/groups/search")
    suspend fun searchGroups(
        @Query("categoryId") categoryId: Int? = null,
        @Query("keyword") keyword: String? = null,
        @Query("cursorId") cursor: Int? = null, // page 대신 cursor 사용
        @Query("size") size: Int? = null,
    ): Response<GroupSearchResponse>

    @GET("api/groups/search/me")
    suspend fun searchMyGroups(
        @Query("page") page: Int? = null,
        @Query("size") size: Int? = null,
        @Query("sort") sort: String? = null
    ): Response<GroupSearchResponse>

    @GET("api/groups/search/joined")
    suspend fun searchJoinedGroups(
        @Query("page") page: Int? = null,
        @Query("size") size: Int? = null,
        @Query("sort") sort: String? = null
    ): Response<GroupSearchResponse>
}
