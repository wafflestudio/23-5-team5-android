package com.example.toyproject5.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.toyproject5.dto.SocialLoginResponse
import com.example.toyproject5.repository.UserRepository
import com.example.toyproject5.ui.screens.auth.SocialVerifyUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SocialVerifyViewModel @Inject constructor(
    private val userRepository: UserRepository,
    savedStateHandle: SavedStateHandle // NavHost에서 보낸 token
) : ViewModel() {

    // NavHost에서 "social_verify/{token}"으로 보낸 값을 꺼냄
    val registerToken: String = checkNotNull(savedStateHandle["token"])

    private val _uiState = MutableStateFlow(SocialVerifyUiState()) // 기존 UiState 재사용 가능
    val uiState = _uiState.asStateFlow()

    var email by mutableStateOf("")
    var authCode by mutableStateOf("")

    private var timerJob: Job? = null
    private val _timeLeft = MutableStateFlow(180) // 180초 = 3분
    val timeLeft = _timeLeft.asStateFlow()

    fun startTimer() {
        timerJob?.cancel() // 기존 타이머가 있다면 취소
        _timeLeft.value = 180
        timerJob = viewModelScope.launch {
            while (_timeLeft.value > 0) {
                delay(1000L)
                _timeLeft.value -= 1
            }
        }
    }

    // 소셜용 메일 발송
    fun sendEmail() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            // Repository에 sendSocialVerifyEmail 함수 구현 필요
            userRepository.sendEmail(email).onSuccess {
                _uiState.update { it.copy(currentStep = 2, isLoading = false) }
                startTimer()
            }.onFailure { /* 에러 처리 */ }
        }
    }

    // 소셜용 코드 확인
    fun verifyCode(onSuccess: (SocialLoginResponse, String) -> Unit) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            userRepository.verifySocialEmailCode(registerToken, email, authCode).onSuccess { response ->
                _uiState.update { it.copy(isLoading = false) }
                onSuccess(response, email) // 인증된 메일을 들고 다음 화면으로!
            }.onFailure { e ->
                _uiState.update { it.copy(isLoading = false, errorMessage = e.message) }
            }
        }
    }

    // 타이머 로직 (기존과 동일)
}