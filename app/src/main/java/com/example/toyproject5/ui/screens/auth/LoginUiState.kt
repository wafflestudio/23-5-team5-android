package com.example.toyproject5.ui.screens.auth

/**
 * 로그인 화면의 UI 상태를 정의하는 데이터 클래스.
 * UI는 오직 이 상태 클래스만 바라보고 화면을 구성한다.
 */
data class LoginUiState(
    val isLoading: Boolean = false,
    val isLoginSuccess: Boolean = false,   // 최종 로그인 완료 (메인으로!)
    val isRegisterNeeded: Boolean = false, // 회원가입 필요 (가입창으로!)
    val registerToken: String? = null,     // 서버에서 받은 임시 토큰
    val email: String? = null,             // 회원가입 시 사용할 이메일
    val errorMessage: String? = null
)