package com.example.toyproject5.repository

import com.example.toyproject5.dto.*
import com.example.toyproject5.network.ReviewApiService
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReviewRepository @Inject constructor(
    private val apiService: ReviewApiService
) {
    suspend fun createReview(request: CreateReviewRequest): Response<ReviewResponse> =
        apiService.createReview(request)

    suspend fun getReview(
        groupId: Int? = null,
        reviewerId: Long? = null,
        revieweeId: Long? = null,
    ): Response<ReviewResponse> =
        apiService.getReview(groupId, reviewerId, revieweeId)

    suspend fun updateReview(request: UpdateReviewRequest): Response<ReviewResponse> =
        apiService.updateReview(request)

    suspend fun deleteReview(reviewId: Long): Response<Unit> =
        apiService.deleteReview(DeleteReviewRequest(reviewId))

    suspend fun searchReviews(
        revieweeId: Long? = null,
        cursor: Int = 0,
        size: Int = 10,
    ): Response<PageResponse<ReviewResponse>> =
        apiService.searchReviews(revieweeId, cursor, size)
}
