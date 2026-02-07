package com.example.toyproject5.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.toyproject5.dto.ReviewResponse
import com.example.toyproject5.dto.UserProfileResponse
import com.example.toyproject5.repository.ReviewRepository
import com.example.toyproject5.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserProfileViewModel @Inject constructor(
    private val userRepository: UserRepository,  // 데이터 소스를 담당하는 저장소
    private val reviewRepository: ReviewRepository
) : ViewModel() {

    // 화면에 보여줄 상태를 담는 StateFlow
    private val _uiState = MutableStateFlow<UserProfileUiState>(UserProfileUiState.Loading)
    val uiState: StateFlow<UserProfileUiState> = _uiState.asStateFlow()

    private val _reviews = MutableStateFlow<List<ReviewResponse>>(emptyList())
    val reviews: StateFlow<List<ReviewResponse>> = _reviews.asStateFlow()

    private val _isReviewsLoading = MutableStateFlow(false)
    val isReviewsLoading: StateFlow<Boolean> = _isReviewsLoading.asStateFlow()

    /**
     * 특정 사용자의 프로필 정보를 가져오는 함수
     * @param userId 조회할 대상의 ID
     */
    fun fetchUserProfile(userId: Long) {
        viewModelScope.launch {
            _uiState.value = UserProfileUiState.Loading
            
            // Fetch profile
            val result = userRepository.getUserProfile(userId.toInt()) // Repository still uses Int, but we use Long in VM for consistency with NavRoute
            // 1. result.isSuccess 로 성공 여부를 확인합니다.
            if (result.isSuccess) {
                // 2. getOrNull()을 통해 결과값(UserProfileResponse)을 가져옵니다.
                val userProfile = result.getOrNull()
                if (userProfile != null) {
                    _uiState.value = UserProfileUiState.Success(userProfile)
                    fetchUserReviews(userId)
                } else {
                    _uiState.value = UserProfileUiState.Error("데이터가 비어있습니다.")
                }
            } else {
                // 3. 실패했을 경우 exceptionOrNull()로 에러 메시지를 추출할 수 있습니다.
                val error = result.exceptionOrNull()?.message ?: "알 수 없는 에러"
                _uiState.value = UserProfileUiState.Error(error)
            }
        }
    }

    private fun fetchUserReviews(userId: Long) {
        viewModelScope.launch {
            _isReviewsLoading.value = true
            val result = reviewRepository.searchReviews(revieweeId = userId)
            if (result.isSuccessful) {
                _reviews.value = result.body()?.content ?: emptyList()
            }
            _isReviewsLoading.value = false
        }
    }
}

sealed class UserProfileUiState {
    object Loading : UserProfileUiState()
    data class Success(val user: UserProfileResponse) : UserProfileUiState()
    data class Error(val message: String) : UserProfileUiState()
}
