package com.example.toyproject5.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.toyproject5.repository.PingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class PingViewModel @Inject constructor(
    private val repository: PingRepository
) : ViewModel() {

    // UI에 보여줄 상태 (처음엔 빈 메시지)
    private val _pingState = MutableStateFlow("버튼을 눌러 서버와 연결하세요.")
    val pingState = _pingState.asStateFlow()

    fun fetchPing() {
        viewModelScope.launch {
            _pingState.value = "연결 중..."
            val result = repository.getPing()

            result.onSuccess {
                _pingState.value = "서버 응답: ${it.message}"
            }.onFailure {
                _pingState.value = "연결 실패: ${it.localizedMessage}"
            }
        }
    }
}