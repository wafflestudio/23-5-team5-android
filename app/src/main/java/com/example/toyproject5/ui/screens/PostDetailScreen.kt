package com.example.toyproject5.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.toyproject5.viewmodel.GroupViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostDetailScreen(
    postId: String, 
    onBack: () -> Unit,
    viewModel: GroupViewModel = hiltViewModel()
) {
    val post by viewModel.selectedGroup.collectAsState()
    val joinedGroups by viewModel.joinedGroups.collectAsState()
    val currentUserId by viewModel.currentUserId.collectAsState()
    val context = LocalContext.current

    // Initialize data
    LaunchedEffect(Unit) {
        viewModel.fetchJoinedGroups()
    }

    // Observe toast events
    LaunchedEffect(Unit) {
        viewModel.toastEvent.collect { message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }

    if (post == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    val currentPost = post!!
    val isClosed = currentPost.status != "RECRUITING"
    val isJoined = joinedGroups.any { it.id == currentPost.id }
    val isMyPost = currentUserId != null && currentPost.leaderId.toLong() == currentUserId
    
    val categoryName = when (currentPost.categoryId) {
        1 -> "스터디"
        2 -> "고시"
        3 -> "취준"
        4 -> "대외활동"
        else -> "기타"
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("공고 상세", fontSize = 18.sp, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.White)
                    .padding(16.dp)
            ) {
                Button(
                    onClick = {
                        if (isJoined) {
                            viewModel.withdrawFromGroup(currentPost.id)
                        } else {
                            viewModel.joinGroup(currentPost.id)
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    enabled = !isClosed && !isMyPost || isJoined, // Allow withdrawing even if closed
                    colors = ButtonDefaults.buttonColors(
                        containerColor = when {
                            isJoined -> Color(0xFFEF4444) // Red for withdraw
                            isClosed -> Color.Gray
                            isMyPost -> Color.Gray
                            else -> Color(0xFF155DFC)
                        }
                    )
                ) {
                    Text(
                        text = when {
                            isJoined -> "나가기"
                            isClosed -> "모집 마감"
                            isMyPost -> "내가 작성한 글"
                            else -> "참여하기"
                        },
                        fontWeight = FontWeight.Bold, 
                        color = Color.White
                    )
                }
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = if (isClosed) Color(0xFFF3F4F6) else Color(0xFFDBEAFE),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Text(
                        text = if (isClosed) "마감됨" else categoryName,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                        color = if (isClosed) Color(0xFF6A7282) else Color(0xFF1447E6),
                        fontSize = 14.sp
                    )
                }
                Text(text = currentPost.createdAt?.take(10) ?: "", color = Color(0xFF6A7282), fontSize = 14.sp)
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = currentPost.groupName,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = if (isClosed) Color(0xFF99A1AF) else Color(0xFF0A0A0A)
            )

            Spacer(modifier = Modifier.height(24.dp))

            DetailItem(
                icon = Icons.Default.Info, 
                label = "방식", 
                value = if (currentPost.isOnline) "온라인" else "오프라인"
            )
            Spacer(modifier = Modifier.height(12.dp))
            DetailItem(
                icon = Icons.Default.Person, 
                label = "정원", 
                value = "${currentPost.capacity ?: "무제한"}"
            )
            Spacer(modifier = Modifier.height(12.dp))
            DetailItem(
                icon = Icons.Default.LocationOn, 
                label = "장소/플랫폼", 
                value = currentPost.location
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(text = "소개", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = currentPost.description,
                fontSize = 16.sp,
                color = Color(0xFF364153),
                lineHeight = 24.sp
            )

            Spacer(modifier = Modifier.height(32.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Person, contentDescription = null, tint = Color(0xFF6A7282))
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "방장 정보", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFF9FAFB), RoundedCornerShape(10.dp))
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape) // 1. 내부의 모든 콘텐츠를 원형으로 자름
                        .background(Color(0xFFD1D5DC), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = currentPost.leaderNickname.take(1).uppercase(),
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )

                    if (!currentPost.leaderProfileImageUrl.isNullOrEmpty()) {
                        AsyncImage(
                            model = currentPost.leaderProfileImageUrl,
                            contentDescription = "방장 프로필",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop,
                            // 로딩 성공 시 자연스럽게 이미지가 나타나도록 합니다.
                            // 이미지가 로드되지 않으면 이 레이어는 투명하게 유지되어 아래의 Text가 보입니다.
                        )
                    }
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(text = currentPost.leaderNickname, fontSize = 16.sp, fontWeight = FontWeight.Medium)
                    Text(text = currentPost.leaderUserName, fontSize = 14.sp, color = Color(0xFF6A7282))
                }
            }
            
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}

@Composable
fun DetailItem(icon: ImageVector, label: String, value: String) {
    Row(verticalAlignment = Alignment.Top) {
        Icon(icon, contentDescription = null, modifier = Modifier.size(20.dp), tint = Color(0xFF6A7282))
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(text = label, fontSize = 14.sp, color = Color(0xFF6A7282))
            Text(text = value, fontSize = 16.sp, color = Color(0xFF0A0A0A))
        }
    }
}
