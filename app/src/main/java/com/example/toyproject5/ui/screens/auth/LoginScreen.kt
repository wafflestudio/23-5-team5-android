package com.example.toyproject5.ui.screens.auth

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.automirrored.filled.Login
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.ui.text.input.VisualTransformation
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.toyproject5.viewmodel.LoginViewModel
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.example.toyproject5.R
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import androidx.compose.foundation.Image
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource

@Composable
fun LoginScreen(
    viewModel: LoginViewModel = hiltViewModel(),
    onLoginSuccess: () -> Unit,
    onSignupClick: () -> Unit,
    onNavigateToSignup: (String, String) -> Unit
) {
    val email by viewModel.email.collectAsState()
    val password by viewModel.password.collectAsState()
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val clientId = stringResource(R.string.default_web_client_id)

    var isPasswordVisible by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }

    // 1. 구글 로그인 설정 (GSO)
    val googleSignInClient = remember {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(clientId)
            .requestEmail()
            .setHostedDomain("snu.ac.kr") // 구글 로그인 설정 단계에서 snu 도메인이 잘 보이도록 유도
            .build()
        GoogleSignIn.getClient(context, gso)
    }

    // 2. 구글 로그인 결과를 처리할 런처
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.getResult(ApiException::class.java)
            val email = account?.email ?: ""
            val idToken = account?.idToken

            if (idToken != null) {
                // 서버로 바로 던집니다. @snu.ac.kr 체크는 서버가 합니다.
                viewModel.loginWithGoogle(idToken, email)
            }
        } catch (e: ApiException) {
            // 에러 처리 로직
        }
    }

    // 1. 로그인 성공 시 메인으로
    LaunchedEffect(uiState.isLoginSuccess) {
        if (uiState.isLoginSuccess) {
            onLoginSuccess()
        }
    }

    // 2. 회원가입 필요 시 가입 화면으로
    LaunchedEffect(uiState.isRegisterNeeded) {
        if (uiState.isRegisterNeeded) {
            // 저장해둔 토큰과 이메일을 가지고 이동!
            onNavigateToSignup(uiState.registerToken!!, uiState.email!!)

            // 💡 이동 후에는 상태를 초기화해주는 게 좋아요 (뒤로가기 시 중복 방지)
            viewModel.resetRegisterState()
        }
    }

    // 3. 에러 발생 시 (4004 포함) 스낵바 출력 및 구글 로그아웃
    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let { msg ->
            // 서버에서 온 "서울대 이메일(@snu.ac.kr)만 가입 가능합니다." 메시지가 뜹니다.
            snackbarHostState.showSnackbar(msg)

            // 중요: 서버에서 거절당했으므로 구글 세션을 끊어줘야
            // 다음에 버튼을 눌렀을 때 계정 선택창이 다시 뜹니다.
            googleSignInClient.signOut()

            viewModel.clearErrorMessage()
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { paddingValues ->

        // 이미지의 어두운 배경색 느낌을 위해 검정색 계열 배경 설정
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // 1. 로고 아이콘 (파란색 원형 아이콘)
            Surface(
                modifier = Modifier.size(60.dp),
                shape = CircleShape,
                color = Color(0xFF2563EB)
            ) {
                Icon(
                    // Login 아이콘 사용
                    imageVector = Icons.AutoMirrored.Filled.Login,
                    contentDescription = "App Logo",
                    tint = Color.White,
                    modifier = Modifier.padding(15.dp) // 내부 아이콘 크기를 맞추기 위해 패딩
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Spacer(modifier = Modifier.height(16.dp))

            // 2. 앱 타이틀 및 설명
            Text(
                text = "팀원 모집 플랫폼",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "대학생 스터디 & 활동 매칭",
                fontSize = 12.sp,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(40.dp))

            // 3. 이메일 입력창
            OutlinedTextField(
                value = email,
                onValueChange = { viewModel.onEmailChanged(it) },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("example@snu.ac.kr") },
                leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                singleLine = true,
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // 4. 비밀번호 입력창
            OutlinedTextField(
                value = password,
                onValueChange = { viewModel.onPasswordChanged(it) },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("비밀번호를 입력하세요") },
                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                // 2. 가시성 상태에 따라 변환 설정
                visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                // 3. 우측에 눈 모양 아이콘 버튼 추가
                trailingIcon = {
                    val image =
                        if (isPasswordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                    val description = if (isPasswordVisible) "비밀번호 숨기기" else "비밀번호 보이기"

                    IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                        Icon(imageVector = image, contentDescription = description)
                    }
                }
            )

            Spacer(modifier = Modifier.height(24.dp))

            // 5. 로그인 버튼
            Button(
                onClick = { viewModel.login() },
                enabled = !uiState.isLoading, // 로딩 중 클릭 방지
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB))
            ) {
                Text(text = "로그인", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 6. 구글 로그인 버튼
            GoogleSignInImageButton(
                onClick = {
                    launcher.launch(googleSignInClient.signInIntent)
                }
            )

            Spacer(modifier = Modifier.height(24.dp))


            // 7. 회원가입 유도 텍스트
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(text = "아직 계정이 없으신가요? ", color = Color.Gray, fontSize = 13.sp)
                Text(
                    text = "회원가입",
                    color = Color(0xFF2563EB),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable { onSignupClick() }
                )
            }
        }
    }
}

@Composable
fun GoogleSignInImageButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // 클릭했을 때 물결(Ripple) 효과를 주기 위한 설정
    val interactionSource = remember { MutableInteractionSource() }

    Surface(
        // 1. Surface를 써서 배경을 투명하게 만듭니다.
        color = Color.Transparent,
        modifier = modifier
            // 2. 이미지 크기에 딱 맞게 감싸줍니다.
            .wrapContentSize()
            // 3. 여기가 핵심! 클릭 기능을 달아줍니다.
            .clickable(
                interactionSource = interactionSource,
                indication = rememberRipple(bounded = true), // 물결 효과 추가
                onClick = onClick
            )
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_google_signin),
            contentDescription = "Google로 로그인",
            // 이미지가 비율을 유지하면서 꽉 차게 설정
            contentScale = ContentScale.Fit,
            // 스크린샷에 나온 높이(40dp)로 설정하면 가장 예쁘게 나옵니다.
            modifier = Modifier.height(40.dp)
        )
    }
}