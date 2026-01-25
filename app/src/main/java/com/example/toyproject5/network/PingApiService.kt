package com.example.toyproject5.network

import com.example.toyproject5.dto.PingResponse
import retrofit2.http.GET

interface PingApiService {
    @GET("api/ping") // 서버의 /api/ping 엔드포인트에 GET 요청을 보냄
    suspend fun getPing(): String // 응답이 그냥 pong이라서...
}