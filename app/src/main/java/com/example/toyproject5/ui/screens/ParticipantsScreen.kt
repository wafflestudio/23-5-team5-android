package com.example.toyproject5.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.border
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.toyproject5.data.mockPosts

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ParticipantsScreen(postId: String, onBack: () -> Unit) {
    val post = mockPosts.find { it.id == postId } ?: return

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
                    text = post.title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0A0A0A)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "총 ${post.participants.size}명의 신청자",
                    fontSize = 14.sp,
                    color = Color(0xFF6A7282)
                )
            }

            if (post.participants.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = "아직 신청자가 없습니다.", color = Color.Gray)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(post.participants) { participant ->
                        ParticipantItem(
                            nickname = participant.nickname,
                            email = participant.email
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ParticipantItem(nickname: String, email: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White, RoundedCornerShape(10.dp))
            .border(1.dp, Color(0xFFE5E7EB), RoundedCornerShape(10.dp))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(Color(0xFFD1D5DC), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = nickname.take(1),
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(text = nickname, fontSize = 16.sp, fontWeight = FontWeight.Medium)
            Text(text = email, fontSize = 14.sp, color = Color(0xFF6A7282))
        }
    }
}


