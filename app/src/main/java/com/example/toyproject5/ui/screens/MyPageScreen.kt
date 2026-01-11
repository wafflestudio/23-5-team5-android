package com.example.toyproject5.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.toyproject5.viewmodel.PingViewModel

// 3. 마이페이지 화면
@Composable
fun MyPageScreen(viewModel: PingViewModel = hiltViewModel()) {
    val pingMessage by viewModel.pingState.collectAsState()

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = "👤 마이페이지 화면", fontSize = 24.sp)

            Spacer(modifier = Modifier.height(30.dp))

            // 서버에서 받아온 메시지 출력
            Text(text = pingMessage, fontSize = 16.sp)

            Spacer(modifier = Modifier.height(16.dp))

            // 버튼 클릭 시 서버 데이터 요청
            Button(onClick = { viewModel.fetchPing() }) {
                Text(text = "서버에 Ping 보내기")
            }
        }
    }
}