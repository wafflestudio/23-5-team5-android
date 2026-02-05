package com.example.toyproject5.ui.screens.auth

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.toyproject5.dto.SocialLoginResponse
import com.example.toyproject5.viewmodel.SocialVerifyViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SocialVerifyScreen(
    onVerificationSuccess: (SocialLoginResponse, String) -> Unit, // 인증된 이메일을 전달하는 콜백
    viewModel: SocialVerifyViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val timeLeft by viewModel.timeLeft.collectAsState()

    Scaffold(
        topBar = { TopAppBar(title = { Text("재학생 인증") }) }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).padding(20.dp)) {
            Text("구글 계정 보안을 위해\n스누메일 인증이 필요합니다.", style = MaterialTheme.typography.titleMedium)

            Spacer(modifier = Modifier.height(24.dp))

            when (uiState.currentStep) {
                1 -> EmailInputStep(
                    email = viewModel.email,
                    onEmailChange = { viewModel.email = it },
                    onNext = { viewModel.sendEmail() },
                    isLoading = uiState.isLoading
                )
                2 -> VerificationStep(
                    email = viewModel.email,
                    authCode = viewModel.authCode,
                    timeLeft = timeLeft,
                    errorMessage = uiState.errorMessage,
                    onAuthCodeChange = { viewModel.authCode = it },
                    onNext = { viewModel.verifyCode(onSuccess = onVerificationSuccess) },
                    onResend = { viewModel.sendEmail() },
                    isLoading = uiState.isLoading
                )
            }
        }
    }
}