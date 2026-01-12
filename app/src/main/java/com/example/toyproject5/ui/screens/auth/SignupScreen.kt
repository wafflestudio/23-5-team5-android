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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.ui.text.input.VisualTransformation

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
    // 이메일이 @snu.ac.kr로 끝나는지 확인하는 로직
    val isSnuEmail = email.endsWith("@snu.ac.kr") && email.length > 10 // @snu.ac.kr (10자) 보다 길어야 함

    Column(modifier = Modifier.fillMaxWidth()) {
        Text("이메일을 입력하세요", style = MaterialTheme.typography.headlineSmall)
        Text(
            text = "학교 이메일(@snu.ac.kr)로 인증이 필요합니다.",
            style = MaterialTheme.typography.bodySmall,
            color = Color.Gray,
            modifier = Modifier.padding(top = 4.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = email,
            onValueChange = onEmailChange,
            label = { Text("이메일") },
            placeholder = { Text("example@snu.ac.kr") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            isError = email.isNotEmpty() && !isSnuEmail // 입력 중인데 형식이 다르면 에러 표시
        )

        // 에러 메시지 (선택 사항)
        if (email.isNotEmpty() && !isSnuEmail) {
            Text(
                text = "@snu.ac.kr 도메인만 사용 가능합니다.",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 4.dp, top = 4.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onNext,
            modifier = Modifier.fillMaxWidth(),
            // 조건을 isSnuEmail로 변경
            enabled = isSnuEmail
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
    // 1. 에러 표시 여부를 결정하는 상태 추가
    var showError by remember { mutableStateOf(false) }

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
            onValueChange = {
                onAuthCodeChange(it)
            },
            label = { Text("인증코드") },
            placeholder = { Text("6자리 인증코드") },
            modifier = Modifier.fillMaxWidth(),
            // 3. showError 상태에 따라 테두리 색상 변경
            isError = showError,
            singleLine = true
        )

        // 4. showError가 true일 때만 에러 메시지 표시
        if (showError) {
            Text(
                text = "인증코드가 일치하지 않습니다",
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 4.dp, top = 4.dp)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                // 5. 버튼 클릭 시점에 정답 확인
                if (authCode == "123456") {
                    showError = false
                    onNext() // 정답이면 다음 단계로
                } else {
                    showError = true // 틀리면 에러 메시지 표시
                }
            },
            modifier = Modifier.fillMaxWidth(),
            // 버튼은 6자리가 입력되었을 때만 활성화 (형식 검사)
            enabled = authCode.length == 6
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
    var isPasswordVisible by remember { mutableStateOf(false) }
    var isConfirmVisible by remember { mutableStateOf(false) }

    // 1. 세부 유효성 검사 로직 정의
    val isPasswordTooShort = password.isNotEmpty() && password.length < 8
    val isPasswordMismatch = password.isNotEmpty() &&
            passwordConfirm.isNotEmpty() &&
            password != passwordConfirm
    val isAllFieldsFilled = nickname.isNotEmpty() &&
            password.isNotEmpty() &&
            passwordConfirm.isNotEmpty()

    // 2. 버튼 활성화 조건: 모든 필드 채움 + 8자 이상 + 비밀번호 일치
    val isButtonEnabled = isAllFieldsFilled &&
            password.length >= 8 &&
            !isPasswordMismatch

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
            singleLine = true,
            // 8자 미만일 때 에러 색상 표시
            isError = isPasswordTooShort,
            visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            trailingIcon = {
                val image = if (isPasswordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                    Icon(imageVector = image, contentDescription = null)
                }
            }
        )

        Spacer(modifier = Modifier.height(12.dp))

        // 비밀번호 확인 입력
        OutlinedTextField(
            value = passwordConfirm,
            onValueChange = onConfirmChange,
            label = { Text("비밀번호 확인") },
            modifier = Modifier.fillMaxWidth(),
            // 일치하지 않을 때 에러 색상 표시
            isError = isPasswordMismatch,
            singleLine = true,
            visualTransformation = if (isConfirmVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            trailingIcon = {
                val image = if (isConfirmVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                IconButton(onClick = { isConfirmVisible = !isConfirmVisible }) {
                    Icon(imageVector = image, contentDescription = null)
                }
            }
        )

        // 3. 에러 메시지 우선순위 노출
        Column(modifier = Modifier.height(24.dp)) { // 메시지 영역 높이 고정 (UI 울렁임 방지)
            if (isPasswordTooShort) {
                Text(
                    text = "비밀번호는 8자리 이상 입력해주세요",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(start = 4.dp, top = 4.dp)
                )
            } else if (isPasswordMismatch) {
                Text(
                    text = "비밀번호가 일치하지 않습니다",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(start = 4.dp, top = 4.dp)
                )
            } else if (!isAllFieldsFilled && (nickname.isNotEmpty() || password.isNotEmpty())) {
                Text(
                    text = "모든 필드를 입력해주세요",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(start = 4.dp, top = 4.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = onComplete,
            modifier = Modifier.fillMaxWidth(),
            // 8자 이상 및 일치 조건이 모두 맞아야 활성화
            enabled = isButtonEnabled
        ) {
            Text("가입 완료")
        }
    }
}