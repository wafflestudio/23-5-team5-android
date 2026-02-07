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
import com.example.toyproject5.data.categoryList
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
    var selectedCategoryId by remember { mutableStateOf(categoryList[0].id) }
    var selectedSubCategoryId by remember { mutableStateOf(categoryList[0].subcategories[0].id) }
    var capacity by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var isOnline by remember { mutableStateOf(false) }

    val selectedCategory = categoryList.find { it.id == selectedCategoryId } ?: categoryList[0]

    LaunchedEffect(errorMessage) {
        errorMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.clearError()
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("공고 작성", fontSize = 18.sp, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.Close, contentDescription = "취소")
                    }
                },
                actions = {
                    TextButton(
                        onClick = {
                            if (groupName.isNotBlank() && description.isNotBlank()) {
                                val request = GroupCreateRequest(
                                    groupName = groupName,
                                    description = description,
                                    categoryId = selectedCategoryId,
                                    subCategoryId = selectedSubCategoryId,
                                    capacity = capacity.toIntOrNull(),
                                    isOnline = isOnline,
                                    location = location
                                )
                                viewModel.createGroup(request) {
                                    Toast.makeText(context, "공고가 등록되었습니다.", Toast.LENGTH_SHORT).show()
                                    onBack()
                                }
                            } else {
                                Toast.makeText(context, "필수 항목을 입력해주세요.", Toast.LENGTH_SHORT).show()
                            }
                        }
                    ) {
                        Text("등록", color = Color(0xFF155DFC), fontWeight = FontWeight.Bold, fontSize = 16.sp)
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
                categoryList.chunked(2).forEach { rowCategories ->
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        rowCategories.forEach { category ->
                            CategoryButton(
                                text = category.name,
                                isSelected = selectedCategoryId == category.id,
                                onClick = { 
                                    selectedCategoryId = category.id
                                    selectedSubCategoryId = category.subcategories[0].id
                                },
                                modifier = Modifier.weight(1f)
                            )
                        }
                        if (rowCategories.size == 1) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Subcategory Selection
            Row {
                Text(
                    text = "상세 카테고리",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
                Text(text = " *", color = Color.Red, fontSize = 14.sp)
            }

            Spacer(modifier = Modifier.height(8.dp))

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                selectedCategory.subcategories.chunked(2).forEach { rowSubcategories ->
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        rowSubcategories.forEach { subcategory ->
                            CategoryButton(
                                text = subcategory.name,
                                isSelected = selectedSubCategoryId == subcategory.id,
                                onClick = { selectedSubCategoryId = subcategory.id },
                                modifier = Modifier.weight(1f)
                            )
                        }
                        if (rowSubcategories.size == 1) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
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

            // Online/Offline Selection
            Text(
                text = "진행 방식",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                CategoryButton(
                    text = "온라인",
                    isSelected = isOnline,
                    onClick = { isOnline = true },
                    modifier = Modifier.weight(1f)
                )
                CategoryButton(
                    text = "오프라인",
                    isSelected = !isOnline,
                    onClick = { isOnline = false },
                    modifier = Modifier.weight(1f)
                )
            }

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
