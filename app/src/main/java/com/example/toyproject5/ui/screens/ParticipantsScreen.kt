package com.example.toyproject5.ui.screens

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.toyproject5.ui.NavRoute
import com.example.toyproject5.viewmodel.ParticipantsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ParticipantsScreen(groupId: Int, groupName: String, onBack: () -> Unit, navController: NavController, viewModel: ParticipantsViewModel = hiltViewModel()) {

    LaunchedEffect(groupId) {
        viewModel.fetchParticipants(groupId)
    }

    val participants by viewModel.participants.collectAsState()
    val error by viewModel.error.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("참여 신청자", fontSize = 18.sp, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .background(Color.White)
        ) {
            // Post Summary
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .background(Color(0xFFF9FAFB), RoundedCornerShape(10.dp))
                    .padding(16.dp)
            ) {
                Text(
                    text = groupName,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0A0A0A)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "총 ${participants.size}명의 신청자",
                    fontSize = 14.sp,
                    color = Color(0xFF6A7282)
                )
            }

            error?.let {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = it, color = Color.Red)
                }
            }

            if (participants.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = "아직 신청자가 없습니다.", color = Color.Gray)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(participants) { participant ->
                        ParticipantItem(
                            nickname = participant.nickname,
                            email = participant.username,
                            profileImageUrl = participant.profileImageUrl,
                            onItemClick = {
                                // 참여자의 고유 ID를 가지고 프로필 화면으로 이동!
                                Log.d("ParticipantsScreen", "User ID: ${participant.userId}")
                                navController.navigate(NavRoute.UserProfile.createRoute(participant.userId.toInt()))
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ParticipantItem(nickname: String, email: String, profileImageUrl: String? = null, onItemClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White, RoundedCornerShape(10.dp))
            .border(1.dp, Color(0xFFE5E7EB), RoundedCornerShape(10.dp))
            .clickable { onItemClick() }
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(Color(0xFFD1D5DC), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            // [수정 포인트] 이미지가 있으면 이미지, 없으면 첫 글자 텍스트
            if (!profileImageUrl.isNullOrEmpty()) {
                AsyncImage(
                    model = profileImageUrl,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize().clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            } else {
                Text(
                    text = nickname.take(1),
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(text = nickname, fontSize = 16.sp, fontWeight = FontWeight.Medium)
            Text(text = email, fontSize = 14.sp, color = Color(0xFF6A7282))
        }
    }
}
