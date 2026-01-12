package com.example.toyproject5.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.toyproject5.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class MyPageViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    val uiState: StateFlow<MyPageState> = userRepository.nickname
        .map { nickname ->
            MyPageState(nickname = nickname)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000), // 5초간 구독자가 없으면 멈춤(자원 절약)
            initialValue = MyPageState() // 초기값
        )

    // 닉네임 변경
    fun updateNickname(newName: String) {
        viewModelScope.launch {
            userRepository.updateNickname(newName)
        }
    }
}

// MyPageState 클래스
data class MyPageState(
    val nickname: String = "냐냐",
    val email: String = "ss@university.ac.kr"
)
