package com.example.toyproject5.ui.screens.auth

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.toyproject5.viewmodel.SplashViewModel

@Composable
fun SplashScreen(
    viewModel: SplashViewModel = hiltViewModel(),
    onNavigateToMain: () -> Unit,
    onNavigateToLogin: () -> Unit
) {
    val isAutoLoginSuccess by viewModel.isAutoLoginSuccess.collectAsState()

    LaunchedEffect(isAutoLoginSuccess) {
        when (isAutoLoginSuccess) {
            true -> onNavigateToMain()  // 성공하면 메인으로
            false -> onNavigateToLogin() // 실패하면 로그인으로
            null -> { /* 아직 판단 중이므로 대기 */ }
        }
    }

    // 화면 중앙에 로딩 표시 (혹은 앱 로고)
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator() // 빙글빙글 도는 로딩 바 TODO: 수정해야 할지도?
    }
}