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
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.toyproject5.data.getCategoryName
import com.example.toyproject5.data.getSubcategoryName
import com.example.toyproject5.dto.GroupResponse
import com.example.toyproject5.viewmodel.GroupViewModel

@Composable
fun MyPostScreen(
    onPostClick: (Int) -> Unit,
    onParticipantsClick: (Int) -> Unit,
    viewModel: GroupViewModel = hiltViewModel()
) {
    val myPosts by viewModel.myGroups.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showExpireDialog by remember { mutableStateOf(false) }
    var selectedGroupId by remember { mutableStateOf<Int?>(null) }

    LaunchedEffect(Unit) {
        viewModel.fetchMyGroups()
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("삭제 확인") },
            text = { Text("그룹을 삭제하시겠습니까?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        selectedGroupId?.let { viewModel.deleteGroup(it) }
                        showDeleteDialog = false
                    },
                    colors = ButtonDefaults.textButtonColors(contentColor = Color.Red)
                ) {
                    Text("삭제")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("취소")
                }
            }
        )
    }

    if (showExpireDialog) {
        AlertDialog(
            onDismissRequest = { showExpireDialog = false },
            title = { Text("마감 확인") },
            text = { Text("그룹 모집을 마감하시겠습니까?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        selectedGroupId?.let { viewModel.expireGroup(it) }
                        showExpireDialog = false
                    }
                ) {
                    Text("확인")
                }
            },
            dismissButton = {
                TextButton(onClick = { showExpireDialog = false }) {
                    Text("취소")
                }
            }
        )
    }

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

        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (myPosts.isEmpty()) {
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
                        group = post, 
                        onClick = { onPostClick(post.id) },
                        onViewParticipants = { onParticipantsClick(post.id) },
                        onExpire = { 
                            selectedGroupId = post.id
                            showExpireDialog = true
                        },
                        onDelete = { 
                            selectedGroupId = post.id
                            showDeleteDialog = true 
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun MyPostCard(
    group: GroupResponse, 
    onClick: () -> Unit,
    onViewParticipants: () -> Unit,
    onExpire: () -> Unit,
    onDelete: () -> Unit
) {
    val isClosed = group.status != "RECRUITING"

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
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    if (isClosed) {
                        Surface(
                            color = Color(0xFFF3F4F6),
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                text = "마감됨",
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                color = Color(0xFF6A7282),
                                fontSize = 12.sp
                            )
                        }
                    } else {
                        Surface(
                            color = Color(0xFFF3E8FF),
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                text = getCategoryName(group.categoryId),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                color = Color(0xFF8200DB),
                                fontSize = 12.sp
                            )
                        }
                        val subcategoryName = getSubcategoryName(group.categoryId, group.subCategoryId)
                        if (subcategoryName != null) {
                            Surface(
                                color = Color(0xFFF3F4F6),
                                shape = RoundedCornerShape(4.dp)
                            ) {
                                Text(
                                    text = subcategoryName,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    color = Color(0xFF4A5565),
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                }
                Text(text = group.createdAt?.take(10) ?: "", color = Color(0xFF6A7282), fontSize = 12.sp)
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = group.groupName,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = if (isClosed) Color(0xFF99A1AF) else Color(0xFF0A0A0A)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = group.description,
                fontSize = 14.sp,
                color = if (isClosed) Color(0xFF99A1AF) else Color(0xFF4A5565),
                maxLines = 2
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onExpire,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(vertical = 8.dp),
                    enabled = !isClosed
                ) {
                    Icon(Icons.Default.Done, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("마감하기", fontSize = 14.sp)
                }
                OutlinedButton(
                    onClick = onDelete,
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
