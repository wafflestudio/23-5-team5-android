package com.example.toyproject5.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.toyproject5.repository.UserRepository
import com.example.toyproject5.dto.SignupRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import javax.inject.Inject

@HiltViewModel
class SignupViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    // UI 상태 관리
    data class SignupUiState(
        val currentStep: Int = 1,
        val isLoading: Boolean = false,
        val isEmailSent: Boolean = false,
        val isVerified: Boolean = false,
        val isSignupSuccess: Boolean = false,
        val errorMessage: String? = null
    )

    private val _uiState = MutableStateFlow(SignupUiState())
    val uiState = _uiState.asStateFlow()

    // 입력 데이터 상태
    var email by mutableStateOf("")
    var authCode by mutableStateOf("")
    var nickname by mutableStateOf("")
    var password by mutableStateOf("")
    var passwordConfirm by mutableStateOf("")
    var major by mutableStateOf("") // 추가 필요
    var studentNumber by mutableStateOf("") // 추가 필요

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

    // 1단계: 인증 메일 발송
    fun sendEmail() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            val result = userRepository.sendEmail(email)
            result.onSuccess {
                startTimer()
                _uiState.update { it.copy(isLoading = false, isEmailSent = true, currentStep = 2) }
            }.onFailure { e ->
                _uiState.update { it.copy(isLoading = false, errorMessage = e.message) }
            }
        }
    }

    // 2단계: 인증번호 확인
    fun verifyCode() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            val result = userRepository.verifyCode(email, authCode)
            result.onSuccess {
                _uiState.update { it.copy(isLoading = false, isVerified = true, currentStep = 3) }
            }.onFailure { e ->
                _uiState.update { it.copy(isLoading = false, errorMessage = e.message) }
            }
        }
    }

    // 인증번호 재발송
    fun resendEmail() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            userRepository.sendEmail(email).onSuccess {
                _uiState.update { it.copy(
                    isLoading = false,
                    errorMessage = "인증번호가 재발송되었습니다." // 스낵바로 표시
                ) }
            }.onFailure { e ->
                _uiState.update { it.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "재발송에 실패했습니다."
                ) }
            }
        }
    }

    // 3단계: 최종 회원가입
    fun completeSignup() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            val request = SignupRequest(
                email = email,
                password = password,
                major = major,
                nickname = nickname
            )
            val result = userRepository.signup(request)
            result.onSuccess {
                _uiState.update { it.copy(isLoading = false, isSignupSuccess = true) }
            }.onFailure { e ->
                _uiState.update { it.copy(isLoading = false, errorMessage = e.message) }
            }
        }
    }

    fun clearErrorMessage() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    fun moveToPreviousStep() {
        if (_uiState.value.currentStep > 1) {
            _uiState.update { it.copy(currentStep = it.currentStep - 1) }
        }
    }
}