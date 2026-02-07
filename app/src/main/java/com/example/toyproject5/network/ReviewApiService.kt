package com.example.toyproject5.network

import com.example.toyproject5.dto.*
import retrofit2.Response
import retrofit2.http.*

interface ReviewApiService {

    @POST("api/reviews")
    suspend fun createReview(
        @Body request: CreateReviewRequest
    ): Response<ReviewResponse>

    @PATCH("api/reviews")
    suspend fun updateReview(
        @Body request: UpdateReviewRequest
    ): Response<ReviewResponse>

    @HTTP(method = "DELETE", path = "api/reviews", hasBody = true)
    suspend fun deleteReview(
        @Body request: DeleteReviewRequest
    ): Response<Unit>

    @GET("api/reviews/search")
    suspend fun searchReviews(
        @Query("groupId") groupId: Int? = null,
        @Query("reviewerId") reviewerId: Long? = null,
        @Query("revieweeId") revieweeId: Long? = null,
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 10,
        @Query("sort") sort: String = "createdAt"
    ): Response<PageResponse<ReviewResponse>>
}
