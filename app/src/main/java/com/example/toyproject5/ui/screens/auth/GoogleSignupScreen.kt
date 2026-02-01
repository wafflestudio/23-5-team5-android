package com.example.toyproject5.ui.screens.auth

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.toyproject5.viewmodel.GoogleSignupViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoogleSignupScreen(
    registerToken: String,
    email: String,
    onSignupSuccess: () -> Unit,
    viewModel: GoogleSignupViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    // 가입 성공 시 처리
    LaunchedEffect(uiState.isSuccess) {
        if (uiState.isSuccess) {
            onSignupSuccess()
        }
    }

    // 에러 발생 시 스낵바 처리
    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let {
            snackbarHostState.showSnackbar(it)
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "구글 회원가입",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1D366D) // SNU 블루 계열
            )

            Text(
                text = "계정: $email",
                fontSize = 14.sp,
                color = Color.Gray,
                modifier = Modifier.padding(top = 8.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            // 학과 입력창
            OutlinedTextField(
                value = viewModel.major,
                onValueChange = { viewModel.major = it },
                label = { Text("학과") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 학번 입력창
            OutlinedTextField(
                value = viewModel.studentNumber,
                onValueChange = { viewModel.studentNumber = it },
                label = { Text("학번 (예: 2026-12345)") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 닉네임 입력창
            OutlinedTextField(
                value = viewModel.nickname,
                onValueChange = { viewModel.nickname = it },
                label = { Text("닉네임") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            // 가입 완료 버튼
            Button(
                onClick = { viewModel.signup(registerToken, email) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB)),
                enabled = !uiState.isLoading &&
                        viewModel.major.isNotBlank() &&
                        viewModel.studentNumber.isNotBlank() &&
                        viewModel.nickname.isNotBlank()
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Text("가입 완료", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}