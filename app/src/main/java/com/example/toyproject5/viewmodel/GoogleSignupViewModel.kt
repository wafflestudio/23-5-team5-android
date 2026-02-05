package com.example.toyproject5.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.toyproject5.repository.UserRepository
import com.example.toyproject5.dto.SocialSignupRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GoogleSignupViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    // 학과, 학번, 닉네임 상태 관리
    var major by mutableStateOf("")
    var studentNumber by mutableStateOf("")
    var nickname by mutableStateOf("")

    private val _uiState = MutableStateFlow(GoogleSignupUiState())
    val uiState = _uiState.asStateFlow()

    fun signup(registerToken: String, email: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            // API 요청 객체 생성
            val request = SocialSignupRequest(
                register_token = registerToken,
                email = email,
                major = major,
                student_number = studentNumber,
                nickname = nickname
            )

            val result = userRepository.googleSignup(request)

            result.onSuccess {
                _uiState.update { it.copy(isLoading = false, isSuccess = true) }
            }.onFailure { e ->
                _uiState.update { it.copy(isLoading = false, errorMessage = e.message) }
            }
        }
    }
}

data class GoogleSignupUiState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessage: String? = null
)