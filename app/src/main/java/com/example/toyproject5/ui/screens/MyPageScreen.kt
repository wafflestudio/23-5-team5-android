package com.example.toyproject5.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.toyproject5.viewmodel.PingViewModel
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

// 3. 마이페이지 화면

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyPageScreen(viewModel: PingViewModel = hiltViewModel()) {
    var showNicknameDialog by remember { mutableStateOf(false) }
    var showProfilePicDialog by remember { mutableStateOf(false) }
    var nickname by remember { mutableStateOf("냐냐") }
    val email = "ss@university.ac.kr"
    val pingMessage by viewModel.pingState.collectAsState() // pingpong api test

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(rememberScrollState())
    ) {
        // Header
        Text(
            text = "마이페이지",
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF0A0A0A)
        )

        // Profile Section
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
                        border = androidx.compose.foundation.BorderStroke(4.dp, Color.White)
                    ) {
                        // Placeholder for profile image
                        Icon(
                            Icons.Default.Person,
                            contentDescription = null,
                            modifier = Modifier.padding(20.dp),
                            tint = Color.White
                        )
                    }
                    IconButton(
                        onClick = { showProfilePicDialog = true },
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .size(32.dp)
                            .background(Color(0xFF2B7FFF), CircleShape)
                    ) {
                        Icon(
                            Icons.Default.Edit,
                            contentDescription = "Edit Profile Pic",
                            modifier = Modifier.size(16.dp),
                            tint = Color.White
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = nickname, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                Text(text = email, fontSize = 14.sp, color = Color(0xFF6A7282))
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Info List
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
                onEditClick = { showNicknameDialog = true }
            )
            InfoItem(
                icon = Icons.Default.Email,
                iconBg = Color(0xFFDCFCE7),
                iconTint = Color(0xFF10B981),
                label = "이메일",
                value = email
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Logout Button
        Button(
            onClick = { /* Logout */ },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .height(48.dp),
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFEF2F2))
        ) {
            Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = null, tint = Color(0xFFE7000B))
            Spacer(modifier = Modifier.width(8.dp))
            Text(text = "로그아웃", color = Color(0xFFE7000B), fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Footer
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFF9FAFB))
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "대학생 팀원 모집 플랫폼", fontSize = 14.sp, color = Color(0xFF4A5565))
            Text(text = "v1.0.0", fontSize = 12.sp, color = Color(0xFF6A7282))
        }
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                // 서버에서 받아온 메시지 출력
                Text(text = pingMessage, fontSize = 16.sp)

                Spacer(modifier = Modifier.height(16.dp))

                // 버튼 클릭 시 서버 데이터 요청
                Button(onClick = { viewModel.fetchPing() }) {
                    Text(text = "서버에 Ping 보내기")
                }
            }
        }
    }

    // Nickname Edit Dialog
    if (showNicknameDialog) {
        var tempNickname by remember { mutableStateOf(nickname) }
        Dialog(onDismissRequest = { showNicknameDialog = false }) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                color = Color.White
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "닉네임 변경", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                        IconButton(onClick = { showNicknameDialog = false }) {
                            Icon(Icons.Default.Close, contentDescription = "Close")
                        }
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(text = "새 닉네임", fontSize = 14.sp, color = Color.Gray)
                    OutlinedTextField(
                        value = tempNickname,
                        onValueChange = { tempNickname = it },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Button(
                        onClick = {
                            nickname = tempNickname
                            showNicknameDialog = false
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEFF6FF))
                    ) {
                        Icon(Icons.Default.Check, contentDescription = null, tint = Color(0xFF155DFC))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = "변경", color = Color(0xFF155DFC), fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }

    // Profile Pic Dialog (Mock)
    if (showProfilePicDialog) {
        Dialog(onDismissRequest = { showProfilePicDialog = false }) {
            Surface(
                modifier = Modifier
                    .fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                color = Color.White
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "프로필 이미지 변경", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                        IconButton(onClick = { showProfilePicDialog = false }) {
                            Icon(Icons.Default.Close, contentDescription = "Close")
                        }
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(128.dp)
                            .background(Color(0xFFF9FAFB), RoundedCornerShape(10.dp))
                            .clickable { /* File upload logic */ },
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.Share, contentDescription = null, tint = Color.Gray)
                            Text(text = "클릭하여 파일 선택", color = Color.Gray, fontSize = 14.sp)
                            Text(text = "JPG, PNG 파일", color = Color.LightGray, fontSize = 12.sp)
                        }
                    }
                }
            }
        }
    }
}

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
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE5E7EB))
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(iconBg, CircleShape),
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
