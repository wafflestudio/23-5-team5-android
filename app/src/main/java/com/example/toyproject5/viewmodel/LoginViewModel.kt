package com.example.toyproject5.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.toyproject5.ui.screens.auth.LoginUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class LoginViewModel @Inject constructor() : ViewModel(){
    val email = MutableStateFlow("")
    val password = MutableStateFlow("")

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    // 이메일 입력 변경 이벤트
    fun onEmailChanged(newValue: String) {
        email.value = newValue
    }

    // 비밀번호 입력 변경 이벤트
    fun onPasswordChanged(newValue: String) {
        password.value = newValue
    }

    // 로그인
    fun login() {
        viewModelScope.launch {
            // 로딩 시작 상태로 업데이트
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            try {
                // TODO: userRepository.login()을 호출
                // 2초간 통신하는 시뮬레이션을 수행합니다.
                delay(2000)

                // 로그인 성공 상태로 업데이트
                _uiState.update { it.copy(isLoading = false, isLoginSuccess = true) }
            } catch (e: Exception) {
                // 실패 시 에러 메시지 업데이트
                _uiState.update {
                    it.copy(isLoading = false, errorMessage = "이메일 또는 비밀번호를 확인해주세요.")
                }
            }
        }
    }

}