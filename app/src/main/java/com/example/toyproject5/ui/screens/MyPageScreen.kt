package com.example.toyproject5.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.toyproject5.viewmodel.MyPageViewModel
import com.example.toyproject5.viewmodel.PingViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyPageScreen(pingViewModel: PingViewModel = hiltViewModel(),
                 myPageViewModel: MyPageViewModel = hiltViewModel()) {
    var showNicknameDialog by remember { mutableStateOf(false) }
    var showProfilePicDialog by remember { mutableStateOf(false) }
    val uiState by myPageViewModel.uiState.collectAsState()
    val pingMessage by pingViewModel.pingState.collectAsState()

    Scaffold(
        topBar = { MyPageHeader() }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color.White)
                .verticalScroll(rememberScrollState())
        ) {
            // 프로필 섹션 (이미지, 닉네임, 이메일)
            ProfileSection(
                nickname = uiState.nickname,
                email = uiState.email,
                onEditProfilePicClick = { showProfilePicDialog = true }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 사용자 정보 리스트 (닉네임, 이메일 카드)
            UserInfoList(
                nickname = uiState.nickname,
                email = uiState.email,
                onNicknameEditClick = { showNicknameDialog = true }
            )

            Spacer(modifier = Modifier.height(32.dp))

            // 로그아웃 버튼
            LogoutButton(onLogoutClick = { /* 로그아웃 로직 처리 */ })

            Spacer(modifier = Modifier.height(32.dp))

            // 푸터 (앱 정보)
            MyPageFooter()

            // 서버 통신 테스트 섹션
            PingPongTestSection(
                pingMessage = pingMessage,
                onPingClick = { pingViewModel.fetchPing() }
            )
        }
    }

    // 3. 다이얼로그
    if (showNicknameDialog) {
        NicknameEditDialog(
            currentNickname = uiState.nickname,
            onDismiss = { showNicknameDialog = false },
            onConfirm = { newName ->
                myPageViewModel.updateNickname(newName)
                showNicknameDialog = false
            }
        )
    }

    if (showProfilePicDialog) {
        ProfilePicDialog(onDismiss = { showProfilePicDialog = false })
    }
}

// 하위 UI 컴포넌트 함수들 (작은 단위로 분리)

/** 상단 헤더 */
@Composable
fun MyPageHeader() {
    Text(
        text = "마이페이지",
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        fontSize = 20.sp,
        fontWeight = FontWeight.Bold,
        color = Color(0xFF0A0A0A)
    )
}

/** 프로필 이미지 및 기본 정보 섹션 */
@Composable
fun ProfileSection(
    nickname: String,
    email: String,
    onEditProfilePicClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(212.dp)
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(Color(0xFFEFF6FF), Color.White)
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Box {
                Surface(
                    modifier = Modifier
                        .size(96.dp)
                        .shadow(4.dp, CircleShape),
                    shape = CircleShape,
                    color = Color.LightGray,
                    border = BorderStroke(4.dp, Color.White)
                ) {
                    Icon(
                        Icons.Default.Person,
                        contentDescription = null,
                        modifier = Modifier.padding(20.dp),
                        tint = Color.White
                    )
                }
                IconButton(
                    onClick = onEditProfilePicClick,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .size(32.dp)
                        .background(Color(0xFF2B7FFF), CircleShape)
                ) {
                    Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.White)
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(text = nickname, fontSize = 20.sp, fontWeight = FontWeight.Bold)
            Text(text = email, fontSize = 14.sp, color = Color(0xFF6A7282))
        }
    }
}

@Composable
fun UserInfoList(
    nickname: String,
    email: String,
    onNicknameEditClick: () -> Unit
) {
    Column(
        modifier = Modifier.padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        InfoItem(
            icon = Icons.Default.Face,
            iconBg = Color(0xFFDBEAFE),
            iconTint = Color(0xFF155DFC),
            label = "닉네임",
            value = nickname,
            onEditClick = onNicknameEditClick
        )
        InfoItem(
            icon = Icons.Default.Email,
            iconBg = Color(0xFFDCFCE7),
            iconTint = Color(0xFF10B981),
            label = "이메일",
            value = email
        )
    }
}

/** 공통 정보 아이템 카드 */
@Composable
fun InfoItem(
    icon: ImageVector,
    iconBg: Color,
    iconTint: Color,
    label: String,
    value: String,
    onEditClick: (() -> Unit)? = null
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, Color(0xFFE5E7EB))
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier.size(40.dp).background(iconBg, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(icon, contentDescription = null, modifier = Modifier.size(20.dp), tint = iconTint)
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(text = label, fontSize = 14.sp, color = Color(0xFF6A7282))
                    Text(text = value, fontSize = 16.sp, fontWeight = FontWeight.Medium)
                }
            }
            if (onEditClick != null) {
                IconButton(onClick = onEditClick) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit", modifier = Modifier.size(16.dp))
                }
            }
        }
    }
}

/** 로그아웃 버튼 */
@Composable
fun LogoutButton(onLogoutClick: () -> Unit) {
    Button(
        onClick = onLogoutClick,
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp).height(48.dp),
        shape = RoundedCornerShape(10.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFEF2F2))
    ) {
        Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = null, tint = Color(0xFFE7000B))
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = "로그아웃", color = Color(0xFFE7000B), fontWeight = FontWeight.Bold)
    }
}

/** 푸터 */
@Composable
fun MyPageFooter() {
    Column(
        modifier = Modifier.fillMaxWidth().background(Color(0xFFF9FAFB)).padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "대학생 팀원 모집 플랫폼", fontSize = 14.sp, color = Color(0xFF4A5565))
        Text(text = "v1.0.0", fontSize = 12.sp, color = Color(0xFF6A7282))
    }
}

/** 서버 통신 테스트 섹션 */
@Composable
fun PingPongTestSection(pingMessage: String, onPingClick: () -> Unit) {
    Box(modifier = Modifier.fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = "Server Response: $pingMessage", fontSize = 14.sp, color = Color.Gray)
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = onPingClick) {
                Text(text = "서버에 Ping 보내기")
            }
        }
    }
}

/** 닉네임 수정 다이얼로그 */
@Composable
fun NicknameEditDialog(
    currentNickname: String,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var tempNickname by remember { mutableStateOf(currentNickname) }
    Dialog(onDismissRequest = onDismiss) {
        Surface(shape = RoundedCornerShape(10.dp), color = Color.White) {
            Column(modifier = Modifier.padding(24.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "닉네임 변경", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    IconButton(onClick = onDismiss) { Icon(Icons.Default.Close, contentDescription = null) }
                }
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = tempNickname,
                    onValueChange = { tempNickname = it },
                    label = { Text("새 닉네임") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = { onConfirm(tempNickname) },
                    modifier = Modifier.fillMaxWidth().height(48.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEFF6FF))
                ) {
                    Text(text = "변경하기", color = Color(0xFF155DFC), fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun ProfilePicDialog(onDismiss: () -> Unit) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(shape = RoundedCornerShape(10.dp), color = Color.White) {
            Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = "프로필 이미지 변경", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(16.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                        .background(Color(0xFFF9FAFB), RoundedCornerShape(10.dp))
                        .clickable { /* 파일 탐색기 로직 */ },
                    contentAlignment = Alignment.Center
                ) {
                    Text("이미지 업로드 (준비 중)", color = Color.Gray)
                }
            }
        }
    }
}