package com.example.toyproject5.repository

import com.example.toyproject5.dto.GroupCreateRequest
import com.example.toyproject5.dto.GroupIdRequest
import com.example.toyproject5.dto.GroupJoinRequest
import com.example.toyproject5.dto.GroupResponse
import com.example.toyproject5.network.GroupApiService
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GroupRepository @Inject constructor(
    private val apiService: GroupApiService
) {
    suspend fun createGroup(request: GroupCreateRequest): Response<Unit> =
        apiService.createGroup(request)

    suspend fun deleteGroup(groupId: Int): Response<Unit> =
        apiService.deleteGroup(GroupIdRequest(groupId))

    suspend fun expireGroup(groupId: Int): Response<Unit> =
        apiService.expireGroup(GroupIdRequest(groupId))

    suspend fun joinGroup(groupId: Int): Response<Unit> =
        apiService.joinGroup(GroupJoinRequest(groupId))

    suspend fun withdrawFromGroup(groupId: Int): Response<Unit> =
        apiService.withdrawFromGroup(GroupJoinRequest(groupId))

    suspend fun searchGroups(categoryId: Int?, keyword: String?): Response<List<GroupResponse>> =
        apiService.searchGroups(categoryId, keyword)

    suspend fun searchMyGroups(): Response<List<GroupResponse>> =
        apiService.searchMyGroups()
}
