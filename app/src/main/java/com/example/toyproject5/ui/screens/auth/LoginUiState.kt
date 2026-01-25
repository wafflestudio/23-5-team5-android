package com.example.toyproject5.ui.screens.auth

/**
 * 로그인 화면의 UI 상태를 정의하는 데이터 클래스.
 * UI는 오직 이 상태 클래스만 바라보고 화면을 구성한다.
 */
data class LoginUiState(
    val isLoading: Boolean = false,      // 로딩 바 표시 여부
    val isLoginSuccess: Boolean = false, // 로그인 성공 시 true (화면 이동 트리거)
    val errorMessage: String? = null     // 에러 발생 시 사용자에게 보여줄 메시지
)