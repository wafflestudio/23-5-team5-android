package com.example.toyproject5.viewmodel

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.toyproject5.repository.UserRepository
import com.example.toyproject5.util.UriUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.delay
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class MyPageViewModel @Inject constructor(
    private val userRepository: UserRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    // 1. [임시 저장소] 사용자가 사진을 고르자마자 '잠시' 담아둘 곳
    private val _tempImageUri = MutableStateFlow<String?>(null)

    val uiState: StateFlow<MyPageState> = combine(
        userRepository.nickname,
        userRepository.email,
        userRepository.profileImageUri,
        _tempImageUri
    ) { nickname, email, savedUri, tempUri ->
        Log.d("UI_STATE_CHECK", "임시(temp): $tempUri, 저장소(saved): $savedUri")
        MyPageState(
            nickname = nickname,
            email = email,
            // [규칙] 임시 사진(tempUri)이 있으면 그걸 쓰고, 없으면 저장된 사진(savedUri)을 쓴다!
            profileImageUrl = tempUri ?: savedUri
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = MyPageState()
    )

    // 이미지 업로드 (낙관적 업데이트)
    fun uploadProfileImage(uri : Uri) {
        // [선조치] 갤러리에서 사진을 받자마자 임시 저장소에 넣습니다.
        _tempImageUri.value = uri.toString()

        // [후보고] 실제 저장 작업은 백그라운드에서 조용히 진행합니다.
        viewModelScope.launch {
            try {
                val imagePart = UriUtil.toMultipartBodyPart(context, uri, "profile_image")
                userRepository.saveProfileImage(uri.toString(), imagePart)
                delay(500)
            } catch (e: Exception) {
                // [실패 시] 저장이 실패하면 임시 값을 지워 원래 사진으로 되돌립니다.
                _tempImageUri.value = null
                e.printStackTrace()
            } finally {
                // 저장이 확인되었거나, 에러가 났을 때만 임시 값을 지웁니다.
                _tempImageUri.value = null
            }
        }
    }

    // 로그아웃
    private val _isLoggedOut = MutableStateFlow(false)
    val isLoggedOut = _isLoggedOut.asStateFlow()

    fun logout() {
        viewModelScope.launch {
            userRepository.logout()
            _isLoggedOut.value = true // 로그아웃 성공 신호!
        }
    }

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
    val email: String = "ss@university.ac.kr",
    val profileImageUrl: String? = null,
)
