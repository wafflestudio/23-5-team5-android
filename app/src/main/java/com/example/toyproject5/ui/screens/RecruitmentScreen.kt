package com.example.toyproject5.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.toyproject5.dto.GroupResponse
import com.example.toyproject5.viewmodel.GroupViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecruitmentScreen(
    onPostClick: (GroupResponse) -> Unit,
    onCreatePostClick: () -> Unit,
    viewModel: GroupViewModel = hiltViewModel()
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategoryId by remember { mutableStateOf<Int?>(null) }
    
    val groups by viewModel.groups.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val isMoreLoading by viewModel.isMoreLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    val listState = rememberLazyListState()

    // Fetch groups on initial load and when search/category changes
    LaunchedEffect(searchQuery, selectedCategoryId) {
        viewModel.searchGroups(
            categoryId = selectedCategoryId, 
            keyword = searchQuery.ifBlank { null },
            isRefresh = true
        )
    }

    // Load more items when scrolled to bottom
    val shouldLoadMore = remember {
        derivedStateOf {
            val lastVisibleItemIndex = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            // Load more when we are near the end of the list
            lastVisibleItemIndex >= groups.size - 3 && groups.isNotEmpty() && !isLoading && !isMoreLoading
        }
    }

    LaunchedEffect(shouldLoadMore.value) {
        if (shouldLoadMore.value) {
            viewModel.searchGroups(
                categoryId = selectedCategoryId,
                keyword = searchQuery.ifBlank { null },
                isRefresh = false
            )
        }
    }

    val categories = listOf(
        CategoryItem("전체", null),
        CategoryItem("스터디", 1),
        CategoryItem("고시", 2),
        CategoryItem("취준", 3),
        CategoryItem("대외활동", 4)
    )

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
            items(categories, key = { it.name }) { category ->
                val isSelected = selectedCategoryId == category.id
                Surface(
                    onClick = { selectedCategoryId = category.id },
                    color = if (isSelected) Color(0xFF155DFC) else Color(0xFFF3F4F6),
                    shape = RoundedCornerShape(20.dp),
                ) {
                    Text(
                        text = category.name,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        color = if (isSelected) Color.White else Color(0xFF364153),
                        fontSize = 14.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (isLoading && groups.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (error != null && groups.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = error ?: "알 수 없는 오류가 발생했습니다.", color = Color.Red)
                    Button(onClick = { viewModel.searchGroups(selectedCategoryId, searchQuery.ifBlank { null }) }) {
                        Text("다시 시도")
                    }
                }
            }
        } else {
            // Post List
            if (groups.isEmpty() && !isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(text = "검색 결과가 없습니다.", color = Color.Gray)
                }
            } else {
                LazyColumn(
                    state = listState,
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(groups, key = { it.id }) { group ->
                        GroupCardItem(group = group, onClick = { onPostClick(group) })
                    }
                    
                    if (isMoreLoading || (isLoading && groups.isNotEmpty())) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(modifier = Modifier.size(24.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}

data class CategoryItem(val name: String, val id: Int?)

@Composable
fun GroupCardItem(group: GroupResponse, onClick: () -> Unit) {
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
                    val categoryName = when (group.categoryId) {
                        1 -> "스터디"
                        2 -> "고시"
                        3 -> "취준"
                        4 -> "대외활동"
                        else -> "기타"
                    }
                    Text(
                        text = categoryName,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        color = Color(0xFF1447E6),
                        fontSize = 12.sp
                    )
                }
                Text(text = group.createdAt?.take(10) ?: "", color = Color(0xFF6A7282), fontSize = 12.sp)
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(text = group.groupName ?: "", fontSize = 16.sp, fontWeight = FontWeight.Bold)

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = group.description ?: "",
                fontSize = 14.sp,
                color = Color(0xFF4A5565),
                maxLines = 2
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                PostInfoRow(icon = Icons.Default.Person, text = "정원: ${group.capacity ?: "무제한"}")
                PostInfoRow(icon = Icons.Default.LocationOn, text = group.location ?: "")
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
