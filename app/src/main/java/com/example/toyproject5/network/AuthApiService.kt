package com.example.toyproject5.network

import com.example.toyproject5.dto.LoginRequest
import com.example.toyproject5.dto.UserResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApiService {
    @POST("api/auth/login")
    suspend fun login(
        @Body loginRequest: LoginRequest
    ): Response<UserResponse>
}
