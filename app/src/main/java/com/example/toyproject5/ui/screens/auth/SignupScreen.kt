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

@Composable
fun VerificationStep(
    email: String,
    authCode: String,
    onAuthCodeChange: (String) -> Unit,
    onNext: () -> Unit
) {
    // 에러 상태 관리 (실제로는 ViewModel에서 검증 결과를 받아와서 처리)
    val isErrorCode = authCode.isNotEmpty() && authCode != "123456" // 예시 로직

    Column(modifier = Modifier.fillMaxWidth()) {
        Text("이메일 인증", style = MaterialTheme.typography.headlineSmall)
        Text(
            text = "$email 로 발송된 인증코드를 입력하세요",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = authCode,
            onValueChange = onAuthCodeChange,
            label = { Text("인증코드") },
            placeholder = { Text("6자리 인증코드") },
            modifier = Modifier.fillMaxWidth(),
            isError = isErrorCode,
            singleLine = true
        )

        // 빨간색 에러 메시지
        if (isErrorCode) {
            Text(
                text = "인증코드가 일치하지 않습니다",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 4.dp, top = 4.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onNext,
            modifier = Modifier.fillMaxWidth(),
            enabled = authCode.length == 6 && !isErrorCode // 6자리이고 에러가 없을 때만 활성화
        ) {
            Text("인증하기")
        }

        TextButton(
            onClick = { /* 재발송 로직 */ },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("인증코드 재발송", color = Color.Gray)
        }
    }
}

@Composable
fun InfoInputStep(
    nickname: String,
    password: String,
    passwordConfirm: String,
    onNicknameChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onConfirmChange: (String) -> Unit,
    onComplete: () -> Unit
) {
    // 유효성 검사 로직
    val isPasswordMismatch = password.isNotEmpty() &&
            passwordConfirm.isNotEmpty() &&
            password != passwordConfirm
    val isAllFieldsFilled = nickname.isNotEmpty() &&
            password.isNotEmpty() &&
            passwordConfirm.isNotEmpty()

    Column(modifier = Modifier.fillMaxWidth()) {
        Text("계정 정보를 입력하세요", style = MaterialTheme.typography.headlineSmall)

        Spacer(modifier = Modifier.height(16.dp))

        // 닉네임 입력
        OutlinedTextField(
            value = nickname,
            onValueChange = onNicknameChange,
            label = { Text("닉네임") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(12.dp))

        // 비밀번호 입력
        OutlinedTextField(
            value = password,
            onValueChange = onPasswordChange,
            label = { Text("비밀번호 (8자 이상)") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(12.dp))

        // 비밀번호 확인 입력
        OutlinedTextField(
            value = passwordConfirm,
            onValueChange = onConfirmChange,
            label = { Text("비밀번호 확인") },
            modifier = Modifier.fillMaxWidth(),
            isError = isPasswordMismatch,
            singleLine = true
        )

        // 에러 메시지 처리 (이미지처럼 아래쪽에 표시)
        if (isPasswordMismatch) {
            Text(
                text = "비밀번호가 일치하지 않습니다",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 4.dp, top = 4.dp)
            )
        } else if (!isAllFieldsFilled && (nickname.isNotEmpty() || password.isNotEmpty())) {
            // 모든 필드가 채워지지 않았을 때의 안내 (이미지 참고)
            Text(
                text = "모든 필드를 입력해주세요",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 4.dp, top = 4.dp)
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = onComplete,
            modifier = Modifier.fillMaxWidth(),
            enabled = isAllFieldsFilled && !isPasswordMismatch
        ) {
            Text("가입 완료")
        }
    }
}