package com.example.toyproject5.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.toyproject5.dto.*
import com.example.toyproject5.repository.GroupRepository
import com.example.toyproject5.repository.ReviewRepository
import com.example.toyproject5.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyJoinedGroupsViewModel @Inject constructor(
    private val groupRepository: GroupRepository,
    private val userRepository: UserRepository,
    private val reviewRepository: ReviewRepository
) : ViewModel() {

    private val _myJoinedGroups = MutableStateFlow<GroupSearchResponse?>(null)
    val myJoinedGroups: StateFlow<GroupSearchResponse?> = _myJoinedGroups

    private val _participants = MutableStateFlow<List<UserSearchResponseDto>>(emptyList())
    val participants: StateFlow<List<UserSearchResponseDto>> = _participants

    private val _myReviews = MutableStateFlow<List<ReviewResponse>>(emptyList())
    val myReviews: StateFlow<List<ReviewResponse>> = _myReviews

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _currentUserId = MutableStateFlow<Long?>(null)
    val currentUserId: StateFlow<Long?> = _currentUserId

    private val _toastMessage = MutableSharedFlow<String>()
    val toastMessage: SharedFlow<String> = _toastMessage

    init {
        viewModelScope.launch {
            _currentUserId.value = userRepository.userId.first()
        }
    }

    fun getMyJoinedGroups() {
        viewModelScope.launch {
            _isLoading.value = true
            val response = groupRepository.searchJoinedGroups()
            if (response.isSuccessful) {
                _myJoinedGroups.value = response.body()
            }
            _isLoading.value = false
        }
    }

    fun loadParticipantsAndReviews(group: GroupResponse) {
        viewModelScope.launch {
            _isLoading.value = true
            val participantsResult = userRepository.getParticipants(group.id)
            val fetchedParticipants = participantsResult.getOrDefault(emptyList()).toMutableList()

            // Ensure leader is in the list
            if (fetchedParticipants.none { it.userId == group.leaderId }) {
                val leader = UserSearchResponseDto(
                    userId = group.leaderId,
                    username = group.leaderUserName,
                    nickname = group.leaderNickname,
                    major = "",
                    studentNumber = "",
                    profileImageUrl = group.leaderProfileImageUrl,
                    bio = null,
                    role = "LEADER",
                    createdAt = ""
                )
                fetchedParticipants.add(0, leader)
            }

            _participants.value = fetchedParticipants

            val userId = _currentUserId.value ?: userRepository.userId.first()
            if (userId != null) {
                _currentUserId.value = userId
                val reviewsResponse = reviewRepository.searchReviews(groupId = group.id, reviewerId = userId)
                if (reviewsResponse.isSuccessful) {
                    _myReviews.value = reviewsResponse.body()?.content ?: emptyList()
                }
            }
            _isLoading.value = false
        }
    }

    fun createReview(groupId: Int, revieweeId: Long, rating: Int, comment: String?) {
        viewModelScope.launch {
            val request = CreateReviewRequest(groupId, revieweeId, rating, comment)
            val response = reviewRepository.createReview(request)
            if (response.isSuccessful) {
                _toastMessage.emit("리뷰가 작성되었습니다.")
                // Find the group object to reload correctly
                _myJoinedGroups.value?.content?.find { it.id == groupId }?.let {
                    loadParticipantsAndReviews(it)
                }
            } else {
                _toastMessage.emit("리뷰 작성에 실패했습니다.")
            }
        }
    }

    fun updateReview(groupId: Int, reviewId: Long, rating: Int?, comment: String?) {
        viewModelScope.launch {
            val request = UpdateReviewRequest(reviewId, rating, comment)
            val response = reviewRepository.updateReview(request)
            if (response.isSuccessful) {
                _toastMessage.emit("리뷰가 수정되었습니다.")
                _myJoinedGroups.value?.content?.find { it.id == groupId }?.let {
                    loadParticipantsAndReviews(it)
                }
            } else {
                _toastMessage.emit("리뷰 수정에 실패했습니다.")
            }
        }
    }

    fun deleteReview(groupId: Int, reviewId: Long) {
        viewModelScope.launch {
            val response = reviewRepository.deleteReview(reviewId)
            if (response.isSuccessful) {
                _toastMessage.emit("리뷰가 삭제되었습니다.")
                _myJoinedGroups.value?.content?.find { it.id == groupId }?.let {
                    loadParticipantsAndReviews(it)
                }
            } else {
                _toastMessage.emit("리뷰 삭제에 실패했습니다.")
            }
        }
    }
}
