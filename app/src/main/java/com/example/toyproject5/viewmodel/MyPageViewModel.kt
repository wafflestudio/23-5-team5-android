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

    // [임시 저장소] 낙관적 업데이트 기조
    private val _tempNickname = MutableStateFlow<String?>(null)
    private val _tempImageUri = MutableStateFlow<String?>(null)

    // 에러 상태 관리 (롤백 시 사용자에게 알리기 위함)
    private val _errorMessage = MutableStateFlow<String?>(null)

    val uiState: StateFlow<MyPageState> = combine(
        listOf(
            userRepository.nickname,      // index 0
            userRepository.email,         // index 1
            userRepository.profileImageUri, // index 2
            _tempImageUri,                // index 3
            _tempNickname,                // index 4
            _errorMessage                 // index 5
        )
    ) { array: Array<Any?> -> // 중요: 파라미터를 6개가 아닌 'array' 하나로 받습니다.

        // 여기서 타입을 하나씩 지정해줍니다 (Casting)
        val nickname = array[0] as String
        val email = array[1] as String
        val savedUri = array[2] as String?
        val tempUri = array[3] as String?
        val tempNick = array[4] as String?
        val error = array[5] as String?

        MyPageState(
            nickname = tempNick ?: nickname,
            email = email,
            profileImageUrl = tempUri ?: savedUri,
            errorMessage = error
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
        if (newName.isBlank()) return

        // [선조치] 서버 응답을 기다리지 않고 UI를 즉시 바꿉니다.
        _tempNickname.value = newName
        _errorMessage.value = null

        viewModelScope.launch {
            try {
                // [본 작업] 서버에 수정 요청 (이미 만들어두신 Result 반환 함수 사용)
                val result = userRepository.updateNickname(newName)

                result.onFailure { exception ->
                    // [실패 시 롤백] 서버 저장 실패 시 임시 값을 지워 원래 이름으로 되돌립니다.
                    _tempNickname.value = null
                    _errorMessage.value = exception.message ?: "닉네임 수정에 실패했습니다."
                }
                // 성공 시에는 Repository 내부에서 DataStore를 업데이트하므로
                // 자연스럽게 정식 데이터(nickname)가 flow를 타고 내려옵니다.
            } catch (e: Exception) {
                _tempNickname.value = null
                _errorMessage.value = "네트워크 오류가 발생했습니다."
            } finally {
                // 작업 완료 후(성공/실패 모두) 임시 값은 비워줍니다.
                // 성공했다면 이미 DataStore의 값이 nickname으로 들어오고 있을 것입니다.
                _tempNickname.value = null
            }
        }
    }

    // 에러 메시지 초기화
    fun clearError() {
        _errorMessage.value = null
    }
}

// MyPageState 클래스
data class MyPageState(
    val nickname: String = "냐냐",
    val email: String = "ss@university.ac.kr",
    val profileImageUrl: String? = null,
    val errorMessage: String? = null
)
