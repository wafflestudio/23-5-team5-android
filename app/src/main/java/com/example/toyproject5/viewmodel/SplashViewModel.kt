package com.example.toyproject5.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.toyproject5.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    // 자동 로그인 결과 상태 (null: 확인 중, true: 성공, false: 실패)
    private val _isAutoLoginSuccess = MutableStateFlow<Boolean?>(null)
    val isAutoLoginSuccess: StateFlow<Boolean?> = _isAutoLoginSuccess.asStateFlow()

    init {
        checkLoginStatus()
    }

    private fun checkLoginStatus() {
        viewModelScope.launch {
            val isValid = userRepository.checkAutoLogin()
            _isAutoLoginSuccess.value = isValid
        }
    }
}