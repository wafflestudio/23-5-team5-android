package com.example.toyproject5.ui.screens.auth

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignupScreen(onSignupComplete: () -> Unit) {
    // 현재 단계를 관리하는 상태 (1, 2, 3)
    var currentStep by remember { mutableStateOf(1) }

    // 입력 데이터들을 관리하는 상태
    var email by remember { mutableStateOf("") }
    var authCode by remember { mutableStateOf("") }
    var nickname by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordConfirm by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("회원가입") },
                navigationIcon = {
                    IconButton(onClick = {
                        if (currentStep > 1) currentStep-- else /* 뒤로가기 처리 */ {}
                    }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "뒤로가기")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            // 1. 단계 인디케이터 (상단 1-2-3 동그라미)
            SignupStepIndicator(currentStep = currentStep)

            Spacer(modifier = Modifier.height(40.dp))

            // 2. 단계별 화면 전환
            when (currentStep) {
                1 -> EmailInputStep(
                    email = email,
                    onEmailChange = { email = it },
                    onNext = { currentStep = 2 }
                )
                2 -> VerificationStep(
                    email = email,
                    authCode = authCode,
                    onAuthCodeChange = { authCode = it },
                    onNext = { currentStep = 3 }
                )
                3 -> InfoInputStep(
                    nickname = nickname,
                    password = password,
                    passwordConfirm = passwordConfirm,
                    onNicknameChange = { nickname = it },
                    onPasswordChange = { password = it },
                    onConfirmChange = { passwordConfirm = it },
                    onComplete = onSignupComplete
                )
            }
        }
    }
}

@Composable
fun SignupStepIndicator(currentStep: Int) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(3) { index ->
            val step = index + 1
            val isActive = step <= currentStep

            // 동그라미 번호
            Surface(
                shape = androidx.compose.foundation.shape.CircleShape,
                color = if (isActive) Color.Blue else Color.LightGray,
                modifier = Modifier.size(30.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(text = step.toString(), color = Color.White)
                }
            }

            // 연결 선 (마지막 단계 제외)
            if (step < 3) {
                Divider(
                    modifier = Modifier.width(40.dp),
                    thickness = 2.dp,
                    color = if (step < currentStep) Color.Blue else Color.LightGray
                )
            }
        }
    }
}

@Composable
fun EmailInputStep(email: String, onEmailChange: (String) -> Unit, onNext: () -> Unit) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text("이메일을 입력하세요", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = email,
            onValueChange = onEmailChange,
            label = { Text("이메일") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = onNext,
            modifier = Modifier.fillMaxWidth(),
            enabled = email.isNotEmpty()
        ) {
            Text("인증코드 발송")
        }
    }
}