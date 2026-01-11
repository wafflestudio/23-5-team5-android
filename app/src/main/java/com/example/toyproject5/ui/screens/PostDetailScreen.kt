package com.example.toyproject5.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.toyproject5.data.mockPosts

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostDetailScreen(postId: String, onBack: () -> Unit) {
    val post = mockPosts.find { it.id == postId } ?: return

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
                    onClick = { /* 참여하기 로직 */ },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF155DFC))
                ) {
                    Text("참여하기", fontWeight = FontWeight.Bold, color = Color.White)
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
                    color = Color(0xFFDBEAFE),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Text(
                        text = post.category,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                        color = Color(0xFF1447E6),
                        fontSize = 14.sp
                    )
                }
                Text(text = post.createdAt, color = Color(0xFF6A7282), fontSize = 14.sp)
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = post.title,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF0A0A0A)
            )

            Spacer(modifier = Modifier.height(24.dp))

            DetailItem(icon = Icons.Default.Info, label = "분야", value = post.field ?: "-")
            Spacer(modifier = Modifier.height(12.dp))
            DetailItem(icon = Icons.Default.DateRange, label = "일시", value = post.date ?: "-")
            Spacer(modifier = Modifier.height(12.dp))
            DetailItem(icon = Icons.Default.LocationOn, label = "장소", value = post.location ?: "-")

            Spacer(modifier = Modifier.height(24.dp))

            Text(text = "소개", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = post.description,
                fontSize = 16.sp,
                color = Color(0xFF364153),
                lineHeight = 24.sp
            )

            Spacer(modifier = Modifier.height(32.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Person, contentDescription = null, tint = Color(0xFF6A7282))
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "작성자", fontSize = 16.sp, fontWeight = FontWeight.Bold)
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
                        .background(Color(0xFFD1D5DC), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = post.authorName.take(1),
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(text = post.authorName, fontSize = 16.sp, fontWeight = FontWeight.Medium)
                    Text(text = post.authorEmail, fontSize = 14.sp, color = Color(0xFF6A7282))
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
