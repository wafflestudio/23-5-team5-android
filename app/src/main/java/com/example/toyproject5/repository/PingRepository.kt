package com.example.toyproject5.repository

import com.example.toyproject5.dto.PingResponse
import com.example.toyproject5.network.PingApiService
import javax.inject.Inject

class PingRepository @Inject constructor(
    private val apiService: PingApiService
) {
    suspend fun getPing(): Result<PingResponse> {
        return try {
            val response = apiService.getPing()
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e) // 네트워크 오류 등 예외 처리
        }
    }
}