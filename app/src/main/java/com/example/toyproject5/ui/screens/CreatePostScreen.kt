package com.example.toyproject5.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.toyproject5.dto.GroupCreateRequest
import com.example.toyproject5.viewmodel.GroupViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreatePostScreen(
    onBack: () -> Unit,
    viewModel: GroupViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val errorMessage by viewModel.error.collectAsState()
    
    var groupName by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("스터디") }
    var capacity by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    val categories = listOf("스터디", "고시", "취준", "대외활동")

    LaunchedEffect(errorMessage) {
        errorMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.clearError()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("공고 작성", fontSize = 18.sp, fontWeight = FontWeight.Bold) },
                actions = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Category Selection
            Row {
                Text(
                    text = "카테고리",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
                Text(text = " *", color = Color.Red, fontSize = 14.sp)
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    CategoryButton(
                        text = "스터디",
                        isSelected = selectedCategory == "스터디",
                        onClick = { selectedCategory = "스터디" },
                        modifier = Modifier.weight(1f)
                    )
                    CategoryButton(
                        text = "고시",
                        isSelected = selectedCategory == "고시",
                        onClick = { selectedCategory = "고시" },
                        modifier = Modifier.weight(1f)
                    )
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    CategoryButton(
                        text = "취준",
                        isSelected = selectedCategory == "취준",
                        onClick = { selectedCategory = "취준" },
                        modifier = Modifier.weight(1f)
                    )
                    CategoryButton(
                        text = "대외활동",
                        isSelected = selectedCategory == "대외활동",
                        onClick = { selectedCategory = "대외활동" },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            InputField(
                label = "제목",
                value = groupName,
                onValueChange = { groupName = it },
                placeholder = "공고 제목을 입력하세요",
                isRequired = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            InputField(
                label = "인원",
                value = capacity,
                onValueChange = { capacity = it },
                placeholder = "10"
            )

            Spacer(modifier = Modifier.height(16.dp))

            InputField(
                label = "장소",
                value = location,
                onValueChange = { location = it },
                placeholder = "예: 중앙도서관 3층"
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "소개글",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp),
                placeholder = { Text("상세한 설명을 입력하세요", color = Color.Gray) },
                shape = RoundedCornerShape(10.dp),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    unfocusedBorderColor = Color(0xFFD1D5DC),
                    focusedBorderColor = Color(0xFF155DFC)
                )
            )

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onBack,
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE5E7EB))
                ) {
                    Text("취소", color = Color(0xFF364153), fontWeight = FontWeight.Bold)
                }
                Button(
                    onClick = {
                        if (groupName.isNotBlank() && description.isNotBlank()) {
                            val request = GroupCreateRequest(
                                groupName = groupName,
                                description = description,
                                categoryId = when (selectedCategory) {
                                    "스터디" -> 1
                                    "고시" -> 2
                                    "취준" -> 3
                                    "대외활동" -> 4
                                    else -> 1
                                },
                                subCategoryId = 1,
                                capacity = capacity.toIntOrNull(),
                                isOnline = location.contains("온라인", ignoreCase = true) || location.contains("online", ignoreCase = true),
                                location = location
                            )
                            viewModel.createGroup(request) {
                                Toast.makeText(context, "공고가 등록되었습니다.", Toast.LENGTH_SHORT).show()
                                onBack()
                            }
                        } else {
                            Toast.makeText(context, "필수 항목을 입력해주세요.", Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF155DFC))
                ) {
                    Text("등록", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun CategoryButton(text: String, isSelected: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Button(
        onClick = onClick,
        modifier = modifier.height(42.dp),
        shape = RoundedCornerShape(10.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isSelected) Color(0xFF155DFC) else Color.White
        ),
        border = if (!isSelected) androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFD1D5DC)) else null
    ) {
        Text(
            text = text,
            color = if (isSelected) Color.White else Color(0xFF364153),
            fontSize = 16.sp
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InputField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    isRequired: Boolean = false
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row {
            Text(text = label, fontSize = 14.sp, fontWeight = FontWeight.Medium)
            if (isRequired) {
                Text(text = " *", color = Color.Red, fontSize = 14.sp)
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text(placeholder, color = Color.Gray) },
            shape = RoundedCornerShape(10.dp),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                unfocusedBorderColor = Color(0xFFD1D5DC),
                focusedBorderColor = Color(0xFF155DFC)
            ),
            singleLine = true
        )
    }
}
