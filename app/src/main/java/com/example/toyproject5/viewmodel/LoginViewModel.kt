package com.example.toyproject5.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.toyproject5.dto.LoginRequest
import com.example.toyproject5.repository.UserRepository
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
class LoginViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel(){
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
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            // Repository에 보낼 요청 객체
            val loginRequest = LoginRequest(
                email = email.value,
                password = password.value
            )

            // Repository의 로그인 함수를 호출하고 결과 받기
            val result = userRepository.login(loginRequest)
            result.onSuccess { user ->
                // 로그인 성공 시
                val infoResult = userRepository.fetchMyInfo()

                infoResult.onSuccess { userMe ->
                    // 최종 성공: 유저 정보까지 다 가져왔을 때 성공 처리
                    _uiState.update {
                        it.copy(isLoading = false, isLoginSuccess = true)
                    }
                }.onFailure { e ->
                    // 로그인엔 성공했지만 정보를 못 가져온 경우
                    _uiState.update {
                        it.copy(isLoading = false, errorMessage = "유저 정보를 불러오는데 실패했습니다.")
                    }
                }

            }.onFailure { exception ->
                // 로그인 실패 시
                _uiState.update {
                    it.copy(isLoading = false, errorMessage = exception.message)
                }
            }
        }
    }
}