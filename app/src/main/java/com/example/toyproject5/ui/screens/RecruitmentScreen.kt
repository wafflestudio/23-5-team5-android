package com.example.toyproject5.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.toyproject5.data.Post
import com.example.toyproject5.data.mockPosts

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecruitmentScreen(
    onPostClick: (String) -> Unit,
    onCreatePostClick: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("전체") }
    val categories = listOf("전체", "스터디", "고시", "취준", "대외활동")

    val filteredPosts = mockPosts.filter { post ->
        (selectedCategory == "전체" || post.category == selectedCategory) &&
                (post.title.contains(searchQuery, ignoreCase = true) ||
                        post.description.contains(searchQuery, ignoreCase = true))
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "팀원 모집", fontSize = 20.sp, fontWeight = FontWeight.Bold)
            IconButton(
                onClick = onCreatePostClick,
                modifier = Modifier
                    .size(36.dp)
                    .background(Color(0xFF155DFC), RoundedCornerShape(18.dp))
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add", tint = Color.White)
            }
        }

        // Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            placeholder = { Text("검색어를 입력하세요", color = Color.Gray) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            shape = RoundedCornerShape(10.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF155DFC),
                unfocusedBorderColor = Color(0xFFD1D5DC)
            ),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Category Chips
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(categories) { category ->
                val isSelected = selectedCategory == category
                Surface(
                    onClick = { selectedCategory = category },
                    color = if (isSelected) Color(0xFF155DFC) else Color(0xFFF3F4F6),
                    shape = RoundedCornerShape(20.dp),
                ) {
                    Text(
                        text = category,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        color = if (isSelected) Color.White else Color(0xFF364153),
                        fontSize = 14.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Post List
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(filteredPosts) { post ->
                PostCardItem(post = post, onClick = { onPostClick(post.id) })
            }
        }
    }
}

@Composable
fun PostCardItem(post: Post, onClick: () -> Unit) {
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
                    color = Color(0xFFDBEAFE),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text = post.category,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        color = Color(0xFF1447E6),
                        fontSize = 12.sp
                    )
                }
                Text(text = post.createdAt, color = Color(0xFF6A7282), fontSize = 12.sp)
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(text = post.title, fontSize = 16.sp, fontWeight = FontWeight.Bold)

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = post.description,
                fontSize = 14.sp,
                color = Color(0xFF4A5565),
                maxLines = 2
            )

            Spacer(modifier = Modifier.height(12.dp))

            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                PostInfoRow(icon = Icons.Default.Star, text = post.field ?: "-")
                PostInfoRow(icon = Icons.Default.DateRange, text = post.date ?: "-")
                PostInfoRow(icon = Icons.Default.LocationOn, text = post.location ?: "-")
            }
        }
    }
}

@Composable
fun PostInfoRow(icon: ImageVector, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, contentDescription = null, modifier = Modifier.size(12.dp), tint = Color(0xFF6A7282))
        Spacer(modifier = Modifier.width(4.dp))
        Text(text = text, fontSize = 12.sp, color = Color(0xFF6A7282))
    }
}
