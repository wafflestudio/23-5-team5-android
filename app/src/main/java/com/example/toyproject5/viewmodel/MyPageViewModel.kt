package com.example.toyproject5.viewmodel

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

@HiltViewModel
class MyPageViewModel @Inject constructor() : ViewModel() {

    // 1. 화면의 전체 상태를 담는 그릇 (Private)
    private val _uiState = MutableStateFlow(MyPageState())

    // 2. UI에서 관찰할 수 있는 공개용 통로 (Public)
    val uiState: StateFlow<MyPageState> = _uiState.asStateFlow()

    fun updateNickname(newName: String) {
        _uiState.update { it.copy(nickname = newName) }
    }
}

// MyPageState 클래스
data class MyPageState(
    val nickname: String = "냐냐",
    val email: String = "ss@university.ac.kr"
)
