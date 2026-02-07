package com.example.toyproject5.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.toyproject5.dto.ReviewResponse
import com.example.toyproject5.dto.UserProfileResponse
import com.example.toyproject5.viewmodel.UserProfileUiState
import com.example.toyproject5.viewmodel.UserProfileViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserProfileScreen(
    userId: Long,
    viewModel: UserProfileViewModel = hiltViewModel(),
    onBackClick: () -> Unit
) {
    // 화면이 처음 켜질 때 해당 userId로 정보를 요청합니다.
    LaunchedEffect(userId) {
        viewModel.fetchUserProfile(userId)
    }

    val uiState by viewModel.uiState.collectAsState()
    val reviews by viewModel.reviews.collectAsState()
    val isReviewsLoading by viewModel.isReviewsLoading.collectAsState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("프로필", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "뒤로가기")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White)
            )
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding).fillMaxSize()) {
            when (val state = uiState) {
                is UserProfileUiState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                is UserProfileUiState.Success -> {
                    val user = state.user
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                    ) {
                        // 1. 프로필 상단 (이미지, 닉네임, 역할)
                        ReadOnlyProfileSection(user)

                        Spacer(modifier = Modifier.height(16.dp))

                        // 2. 상세 정보 리스트 (전공, 학번, 자기소개 등)
                        ReadOnlyUserInfoList(user)

                        Spacer(modifier = Modifier.height(32.dp))

                        // 3. 리뷰 섹션
                        ReviewSection(reviews, isReviewsLoading)

                        Spacer(modifier = Modifier.height(32.dp))
                    }
                }
                is UserProfileUiState.Error -> {
                    Text(text = state.message, modifier = Modifier.align(Alignment.Center), color = Color.Red)
                }
            }
        }
    }
}

/** 타인 프로필 상단: 수정 버튼 제거 버전 */
@Composable
fun ReadOnlyProfileSection(user: UserProfileResponse) {
    Box(
        modifier = Modifier.fillMaxWidth().height(200.dp)
            .background(Brush.linearGradient(colors = listOf(Color(0xFFF8FAFC), Color.White))),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Surface(
                modifier = Modifier.size(100.dp).shadow(4.dp, CircleShape),
                shape = CircleShape,
                color = Color(0xFFD1D5DC), // 기본 배경색 (아이콘과 잘 어울리는 회색)
                border = BorderStroke(2.dp, Color.White)
            ) {
                if (!user.profileImageUrl.isNullOrEmpty()) {
                    AsyncImage(
                        model = user.profileImageUrl,
                        contentDescription = "프로필 이미지",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    // 이미지 주소가 없을 때 보여줄 기본 사람 아이콘
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "기본 프로필",
                        modifier = Modifier.padding(20.dp), // 아이콘 크기 조절을 위한 패딩
                        tint = Color.White
                    )
                }
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(text = user.nickname, fontSize = 22.sp, fontWeight = FontWeight.Bold)
            Text(text = user.role, fontSize = 14.sp, color = Color(0xFF2B7FFF), fontWeight = FontWeight.SemiBold)
        }
    }
}

/** 정보 리스트: 클릭 이벤트 및 수정 아이콘 제거 */
@Composable
fun ReadOnlyUserInfoList(user: UserProfileResponse) {
    Column(
        modifier = Modifier.padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        InfoItem(Icons.Default.School, Color(0xFFFEF3C7), Color(0xFFD97706), "전공", user.major ?: "미지정")
        InfoItem(Icons.Default.Badge, Color(0xFFDBEAFE), Color(0xFF155DFC), "학번", user.studentNumber ?: "비공개")
        InfoItem(Icons.Default.Info, Color(0xFFF3E8FF), Color(0xFF7C3AED), "자기소개", user.bio ?: "안녕하세요!")
        InfoItem(
            icon = Icons.Default.Email,
            iconBg = Color(0xFFDCFCE7),
            iconTint = Color(0xFF10B981),
            label = "이메일",
            value = user.username // 서버 응답의 username을 이메일로 표시
        )
    }
}

@Composable
fun ReviewSection(reviews: List<ReviewResponse>, isLoading: Boolean) {
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        Text(text = "받은 리뷰", fontSize = 18.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(12.dp))

        if (isLoading) {
            Box(modifier = Modifier.fillMaxWidth().padding(16.dp), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp))
            }
        } else if (reviews.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFF9FAFB), RoundedCornerShape(10.dp))
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "아직 받은 리뷰가 없습니다.", color = Color.Gray, fontSize = 14.sp)
            }
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                reviews.forEach { review ->
                    ReviewItem(review)
                }
            }
        }
    }
}

@Composable
fun ReviewItem(review: ReviewResponse) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF9FAFB)),
        border = BorderStroke(1.dp, Color(0xFFE5E7EB))
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = review.reviewerNickname ?: "익명", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Row {
                    repeat(5) { index ->
                        Icon(
                            imageVector = if (index < review.rating) Icons.Default.Star else Icons.Outlined.Star,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp),
                            tint = Color(0xFFFFD700)
                        )
                    }
                }
            }
            if (!review.comment.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = review.comment, fontSize = 14.sp, color = Color(0xFF364153))
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = review.createdAt.take(10),
                fontSize = 12.sp,
                color = Color.Gray,
                modifier = Modifier.align(Alignment.End)
            )
        }
    }
}
