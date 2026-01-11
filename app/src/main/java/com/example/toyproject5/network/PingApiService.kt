package com.example.toyproject5.network

import com.example.toyproject5.dto.PingResponse
import retrofit2.http.GET

interface PingApiService {
    @GET("ping") // 서버의 /ping 엔드포인트에 GET 요청을 보냄
    suspend fun getPing(): PingResponse
}