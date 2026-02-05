package com.example.toyproject5.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.toyproject5.dto.LoginRequest
import com.example.toyproject5.repository.AuthRepository
import com.example.toyproject5.ui.screens.auth.LoginUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel(){
    val email = MutableStateFlow("")
    val password = MutableStateFlow("")

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    // 화면 이동을 제어하기 위한 이벤트 (예: SharedFlow)
    private val _navigationEvent = MutableSharedFlow<LoginNavigation>()
    val navigationEvent = _navigationEvent.asSharedFlow()

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
            val result = authRepository.login(loginRequest)
            result.onSuccess {
                // 로그인 성공 시
                syncUserInfo()

            }.onFailure { exception ->
                // 로그인 실패 시
                _uiState.update {
                    it.copy(isLoading = false, errorMessage = exception.message)
                }
            }
        }
    }

    // 구글 로그인
    fun loginWithGoogle(idToken: String, email: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            // UserRepository에 구글 로그인 처리 함수
            val result = authRepository.handleGoogleAuth(idToken, email)

            result.onSuccess { response ->
                if (response.type == "LOGIN") {
                    // 기존 유저: 로그인 성공 상태로 업데이트
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isLoginSuccess = true
                        )
                    }
                }else if (response.type == "REGISTER") {
                    // 신규 유저: 가입 필요한 상태로 업데이트
                    _uiState.update { it.copy(
                        isLoading = false,
                        isRegisterNeeded = true,
                        registerToken = response.token,
                        email = email
                    ) }
                }
            }.onFailure { exception ->
                _uiState.update { it.copy(
                    isLoading = false,
                    errorMessage = exception.message
                ) }
            }
        }
    }

    private suspend fun syncUserInfo() {
        authRepository.fetchMyInfo()
            .onSuccess {
                _uiState.update { it.copy(isLoading = false, isLoginSuccess = true) }
            }
            .onFailure { e ->
                _uiState.update { it.copy(isLoading = false, errorMessage = "프로필 정보 동기화 실패") }
            }
    }

    fun resetRegisterState() {
        _uiState.update { it.copy(
            isRegisterNeeded = false,
            registerToken = null
            // email은 가입창에서 써야 하므로 유지하거나 가입창 진입 후 지웁니다.
        ) }
    }

    // 로그인 에러 메시지
    private val _errorMessage = mutableStateOf<String?>(null)
    val errorMessage: State<String?> = _errorMessage

    fun showErrorMessage(message: String) {
        _errorMessage.value = message
    }

    fun clearErrorMessage() {
        _errorMessage.value = null
    }
}

// 네비게이션 이벤트 정의
sealed class LoginNavigation {
    data class NavigateToSignup(val registerToken: String, val email: String) : LoginNavigation()
    object NavigateToMain : LoginNavigation()
}