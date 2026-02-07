package com.example.toyproject5.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.toyproject5.dto.GroupResponse
import com.example.toyproject5.dto.ReviewResponse
import com.example.toyproject5.dto.UserSearchResponseDto
import com.example.toyproject5.viewmodel.MyJoinedGroupsViewModel
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyJoinedGroupsScreen(
    onPostClick: (GroupResponse) -> Unit,
    viewModel: MyJoinedGroupsViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val myJoinedGroupsResponse by viewModel.myJoinedGroups.collectAsState()
    val participants by viewModel.participants.collectAsState()
    val myReviews by viewModel.myReviews.collectAsState()
    val currentUserId by viewModel.currentUserId.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    val groups = myJoinedGroupsResponse?.content ?: emptyList()

    var selectedGroupForReviews by remember { mutableStateOf<GroupResponse?>(null) }
    var showReviewDialog by remember { mutableStateOf(false) }
    var reviewToEdit by remember { mutableStateOf<ReviewResponse?>(null) }
    var revieweeForNewReview by remember { mutableStateOf<UserSearchResponseDto?>(null) }

    LaunchedEffect(Unit) {
        viewModel.getMyJoinedGroups()
    }

    LaunchedEffect(viewModel.toastMessage) {
        viewModel.toastMessage.collectLatest { message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
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
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "내가 참여한 팀", fontSize = 20.sp, fontWeight = FontWeight.Bold)
        }

        if (groups.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = "참여 중인 팀이 없습니다.", color = Color.Gray)
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(groups, key = { it.id }) { group ->
                    Column {
                        GroupCardItem(group = group, onClick = { onPostClick(group) })
                        Button(
                            onClick = {
                                selectedGroupForReviews = group
                                viewModel.loadParticipantsAndReviews(group)
                            },
                            modifier = Modifier.padding(top = 4.dp),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF0F0F0), contentColor = Color.Black)
                        ) {
                            Text("멤버 리뷰 관리", fontSize = 12.sp)
                        }
                    }
                }
            }
        }
    }

    // Participants and Reviews Bottom Sheet or Dialog
    if (selectedGroupForReviews != null) {
        ModalBottomSheet(
            onDismissRequest = { selectedGroupForReviews = null },
            sheetState = rememberModalBottomSheetState()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .padding(bottom = 32.dp)
            ) {
                Text(
                    text = "${selectedGroupForReviews?.groupName} 멤버",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                } else {
                    participants.forEach { member ->
                        if (member.userId != currentUserId) {
                            val existingReview = myReviews.find { it.revieweeId == member.userId }
                            MemberReviewItem(
                                member = member,
                                existingReview = existingReview,
                                isLeader = member.userId == selectedGroupForReviews?.leaderId,
                                onReviewClick = {
                                    revieweeForNewReview = member
                                    reviewToEdit = null
                                    showReviewDialog = true
                                },
                                onEditReviewClick = {
                                    reviewToEdit = existingReview
                                    revieweeForNewReview = null
                                    showReviewDialog = true
                                },
                                onDeleteReviewClick = {
                                    existingReview?.let {
                                        viewModel.deleteReview(selectedGroupForReviews!!.id, it.id)
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    if (showReviewDialog) {
        ReviewDialog(
            reviewToEdit = reviewToEdit,
            revieweeName = revieweeForNewReview?.nickname ?: reviewToEdit?.revieweeNickname ?: "",
            onDismiss = { showReviewDialog = false },
            onSubmit = { rating, comment ->
                selectedGroupForReviews?.let { group ->
                    if (reviewToEdit != null) {
                        viewModel.updateReview(group.id, reviewToEdit!!.id, rating, comment)
                    } else if (revieweeForNewReview != null) {
                        viewModel.createReview(group.id, revieweeForNewReview!!.userId, rating, comment)
                    }
                }
                showReviewDialog = false
            }
        )
    }
}

@Composable
fun MemberReviewItem(
    member: UserSearchResponseDto,
    existingReview: ReviewResponse?,
    isLeader: Boolean,
    onReviewClick: () -> Unit,
    onEditReviewClick: () -> Unit,
    onDeleteReviewClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = member.profileImageUrl ?: "https://via.placeholder.com/150",
            contentDescription = null,
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape),
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = member.nickname, fontWeight = FontWeight.Medium)
                if (isLeader) {
                    Spacer(modifier = Modifier.width(4.dp))
                    Surface(
                        color = Color(0xFFE0E7FF),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = "리더",
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp),
                            color = Color(0xFF4338CA),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
            if (existingReview != null) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    repeat(5) { index ->
                        Icon(
                            imageVector = if (index < existingReview.rating) Icons.Filled.Star else Icons.Outlined.Star,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp),
                            tint = Color(0xFFFFD700)
                        )
                    }
                    if (!existingReview.comment.isNullOrBlank()) {
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = existingReview.comment, fontSize = 12.sp, color = Color.Gray, maxLines = 1)
                    }
                }
            }
        }

        if (existingReview == null) {
            TextButton(onClick = onReviewClick) {
                Text("리뷰 작성")
            }
        } else {
            IconButton(onClick = onEditReviewClick) {
                Icon(Icons.Default.Edit, contentDescription = "Edit", tint = Color.Gray, modifier = Modifier.size(20.dp))
            }
            IconButton(onClick = onDeleteReviewClick) {
                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Red, modifier = Modifier.size(20.dp))
            }
        }
    }
}

@Composable
fun ReviewDialog(
    reviewToEdit: ReviewResponse?,
    revieweeName: String,
    onDismiss: () -> Unit,
    onSubmit: (Int, String) -> Unit
) {
    var rating by remember { mutableIntStateOf(reviewToEdit?.rating ?: 5) }
    var comment by remember { mutableStateOf(reviewToEdit?.comment ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = if (reviewToEdit == null) "$revieweeName 님 리뷰 작성" else "$revieweeName 님 리뷰 수정") },
        text = {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    repeat(5) { index ->
                        val isSelected = index < rating
                        IconButton(onClick = { rating = index + 1 }) {
                            Icon(
                                imageVector = if (isSelected) Icons.Filled.Star else Icons.Outlined.Star,
                                contentDescription = null,
                                tint = if (isSelected) Color(0xFFFFD700) else Color.LightGray,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = comment,
                    onValueChange = { comment = it },
                    label = { Text("리뷰 내용 (선택사항)") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3
                )
            }
        },
        confirmButton = {
            Button(onClick = { onSubmit(rating, comment) }) {
                Text("확인")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("취소")
            }
        }
    )
}
