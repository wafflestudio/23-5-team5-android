package com.example.toyproject5.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.toyproject5.dto.GroupResponse
import com.example.toyproject5.viewmodel.MyJoinedGroupsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyJoinedGroupsScreen(
    onPostClick: (GroupResponse) -> Unit,
    viewModel: MyJoinedGroupsViewModel = hiltViewModel()
) {
    val myJoinedGroupsResponse by viewModel.myJoinedGroups.collectAsState()
    val groups = myJoinedGroupsResponse?.content ?: emptyList()

    LaunchedEffect(Unit) {
        viewModel.getMyJoinedGroups()
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
                    GroupCardItem(group = group, onClick = { onPostClick(group) })
                }
            }
        }
    }
}
