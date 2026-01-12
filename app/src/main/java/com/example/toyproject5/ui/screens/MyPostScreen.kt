package com.example.toyproject5.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.toyproject5.data.Post
import com.example.toyproject5.data.mockPosts

@Composable
fun MyPostScreen(
    onPostClick: (String) -> Unit,
    onParticipantsClick: (String) -> Unit
) {
    val myPosts = mockPosts.filter { it.userId == "user1" }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "내가 작성한 공고",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0A0A0A)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "총 ${myPosts.size}개의 공고",
                fontSize = 14.sp,
                color = Color(0xFF6A7282)
            )
        }

        if (myPosts.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "작성한 공고가 없습니다",
                    color = Color(0xFF6A7282),
                    textAlign = TextAlign.Center
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(myPosts) { post ->
                    MyPostCard(
                        post = post, 
                        onClick = { onPostClick(post.id) },
                        onViewParticipants = { onParticipantsClick(post.id) }
                    )
                }
            }
        }
    }
}

@Composable
fun MyPostCard(
    post: Post, 
    onClick: () -> Unit,
    onViewParticipants: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE5E7EB))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = if (post.isClosed) Color(0xFFF3F4F6) else Color(0xFFF3E8FF),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text = if (post.isClosed) "마감됨" else post.category,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        color = if (post.isClosed) Color(0xFF6A7282) else Color(0xFF8200DB),
                        fontSize = 12.sp
                    )
                }
                Text(text = post.createdAt, color = Color(0xFF6A7282), fontSize = 12.sp)
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = post.title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = if (post.isClosed) Color(0xFF99A1AF) else Color(0xFF0A0A0A)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = post.description,
                fontSize = 14.sp,
                color = if (post.isClosed) Color(0xFF99A1AF) else Color(0xFF4A5565),
                maxLines = 2
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = { /* 마감하기 로직 */ },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(vertical = 8.dp)
                ) {
                    Icon(Icons.Default.Done, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("마감하기", fontSize = 14.sp)
                }
                OutlinedButton(
                    onClick = { /* 삭제하기 로직 */ },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(vertical = 8.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Red)
                ) {
                    Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("삭제하기", fontSize = 14.sp)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Surface(
                onClick = onViewParticipants,
                modifier = Modifier.fillMaxWidth(),
                color = Color(0xFFF9FAFB),
                shape = RoundedCornerShape(8.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = Color(0xFF6A7282)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "참여 신청자 보기",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFF364153)
                        )
                    }
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                        tint = Color(0xFF99A1AF)
                    )
                }
            }
        }
    }
}
