package com.example.toyproject5.viewmodel

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.toyproject5.dto.ReviewResponse
import com.example.toyproject5.repository.ReviewRepository
import com.example.toyproject5.repository.UserRepository
import com.example.toyproject5.util.UriUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
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
    private val reviewRepository: ReviewRepository,
    @ApplicationContext private val context: Context
) : ViewModel() {

    // [임시 저장소] 낙관적 업데이트 기조
    private val _tempNickname = MutableStateFlow<String?>(null)
    private val _tempImageUri = MutableStateFlow<String?>(null)
    private val _tempMajor = MutableStateFlow<String?>(null)
    private val _tempBio = MutableStateFlow<String?>(null)

    // 에러 상태 관리 (롤백 시 사용자에게 알리기 위함)
    private val _errorMessage = MutableStateFlow<String?>(null)

    // 토스트 이벤트 관리
    private val _eventFlow = MutableSharedFlow<MyPageEvent>()
    val eventFlow = _eventFlow.asSharedFlow()

    private val _reviews = MutableStateFlow<List<ReviewResponse>>(emptyList())
    val reviews: StateFlow<List<ReviewResponse>> = _reviews.asStateFlow()

    private val _isReviewsLoading = MutableStateFlow(false)
    val isReviewsLoading: StateFlow<Boolean> = _isReviewsLoading.asStateFlow()

    init {
        viewModelScope.launch {
            val userId = userRepository.userId.first()
            if (userId != null) {
                fetchReviews(userId)
            }
        }
    }

    private fun fetchReviews(userId: Long) {
        viewModelScope.launch {
            _isReviewsLoading.value = true
            try {
                val response = reviewRepository.searchReviews(revieweeId = userId)
                if (response.isSuccessful) {
                    _reviews.value = response.body()?.content ?: emptyList()
                }
            } catch (e: Exception) {
                Log.e("MyPageViewModel", "Failed to fetch reviews", e)
            } finally {
                _isReviewsLoading.value = false
            }
        }
    }

    val uiState: StateFlow<MyPageState> = combine(
        listOf(
            userRepository.nickname,       // 0
            userRepository.email,          // 1
            userRepository.profileImageUri, // 2
            userRepository.major,          // 3
            userRepository.bio,            // 4
            _tempNickname,                 // 5
            _tempImageUri,                 // 6
            _tempMajor,                    // 7
            _tempBio,                      // 8
            _errorMessage                  // 9
        )
    ) { array: Array<Any?> ->

        if (isLoggingOut) {
            return@combine this@MyPageViewModel.uiState.value
        }
        val nickname = array[0] as String
        val email = array[1] as String
        val savedUri = array[2] as String?
        val savedMajor = array[3] as String?
        val savedBio = array[4] as String?

        val tempNick = array[5] as String?
        val tempUri = array[6] as String?
        val tempMajor = array[7] as String?
        val tempBio = array[8] as String?
        val error = array[9] as String?

        MyPageState(
            nickname = tempNick ?: nickname,
            email = email,
            profileImageUrl = tempUri ?: savedUri,
            major = tempMajor ?: savedMajor ?: "전공을 입력해주세요",
            bio = tempBio ?: savedBio ?: "자기소개를 입력해주세요",
            errorMessage = error
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = MyPageState()
    )

    sealed class MyPageEvent {
        data class ShowToast(val message: String) : MyPageEvent()
    }

    // 이미지 업로드 (낙관적 업데이트)
    fun uploadProfileImage(uri : Uri) {
        // [선조치] 갤러리에서 사진을 받자마자 임시 저장소에 넣습니다.
        _tempImageUri.value = uri.toString()

        // [후보고] 실제 저장 작업은 백그라운드에서 조용히 진행합니다.
        viewModelScope.launch {
            try {
                val imagePart = UriUtil.toMultipartBodyPart(context, uri, "profile_image")
                userRepository.saveProfileImage(uri.toString(), imagePart)

                // 성공 알림
                _eventFlow.emit(MyPageEvent.ShowToast("성공적으로 저장되었습니다"))
            } catch (e: Exception) {
                // [실패 시] 저장이 실패하면 임시 값을 지워 원래 사진으로 되돌립니다.
                _tempImageUri.value = null

                // 실패 알림
                _eventFlow.emit(MyPageEvent.ShowToast("저장 실패: ${e.localizedMessage}"))
            } finally {
                // 저장이 확인되었거나, 에러가 났을 때만 임시 값을 지웁니다.
                _tempImageUri.value = null
            }
        }
    }

    // 로그아웃
    private val _isLoggedOut = MutableStateFlow(false)
    val isLoggedOut = _isLoggedOut.asStateFlow()
    private var isLoggingOut = false

    fun logout() {
        if (isLoggingOut) return
        isLoggingOut = true
        viewModelScope.launch {
            userRepository.logout()
            _isLoggedOut.value = true
        }
    }

    // 닉네임 변경
    fun updateNickname(newName: String) {
        if (newName.isBlank()) return

        // [선조치] 서버 응답을 기다리지 않고 UI 변경
        _tempNickname.value = newName
        _errorMessage.value = null
        viewModelScope.launch {
            try {
                val result = userRepository.updateNickname(newName)
                result.onSuccess {
                    // 성공시 토스트
                    _eventFlow.emit(MyPageEvent.ShowToast("성공적으로 저장되었습니다"))
                }.onFailure { exception ->
                    // [실패 시 롤백]
                    _tempNickname.value = null
                    _errorMessage.value = exception.message ?: "닉네임 수정에 실패했습니다."

                    // 실패시 토스트
                    _eventFlow.emit(MyPageEvent.ShowToast("저장 실패: ${exception.message}"))
                }
            } catch (e: Exception) {
                _tempNickname.value = null
                _errorMessage.value = "네트워크 오류가 발생했습니다."
                _eventFlow.emit(MyPageEvent.ShowToast("저장 실패: 네트워크 오류가 발생했습니다."))
            } finally {
                _tempNickname.value = null
            }
        }
    }

    // 전공 변경
    fun updateMajor(newMajor: String) {
        if (newMajor.isBlank()) return

        // [선조치] 서버 응답을 기다리지 않고 UI 변경
        _tempMajor.value = newMajor
        _errorMessage.value = null
        viewModelScope.launch {
            try {
                val result = userRepository.updateMajor(newMajor)
                result.onSuccess {
                    _eventFlow.emit(MyPageEvent.ShowToast("전공이 성공적으로 저장되었습니다"))
                }.onFailure {
                    _tempMajor.value = null
                    _errorMessage.value = "전공 수정에 실패했습니다."
                    _eventFlow.emit(MyPageEvent.ShowToast("전공 수정에 실패했습니다."))
                }
            } catch (e: Exception) {
                _tempMajor.value = null
                _errorMessage.value = "네트워크 오류가 발생했습니다."
                _eventFlow.emit(MyPageEvent.ShowToast("저장 실패: 네트워크 오류가 발생했습니다."))
            } finally {
                _tempMajor.value = null
            }
        }
    }

    // 자기소개 변경
    fun updateBio(newBio: String) {

        // [선조치] 서버 응답을 기다리지 않고 UI 변경
        _tempBio.value = newBio
        _errorMessage.value = null
        viewModelScope.launch {
            try {
                val result = userRepository.updateBio(newBio)
                result.onSuccess {
                    _eventFlow.emit(MyPageEvent.ShowToast("자기소개가 성공적으로 저장되었습니다"))
                }.onFailure {
                    _tempBio.value = null
                    _errorMessage.value = "자기소개 수정에 실패했습니다."
                    _eventFlow.emit(MyPageEvent.ShowToast("자기소개 수정에 실패했습니다."))
                }
            } catch (e: Exception) {
                _tempBio.value = null
                _errorMessage.value = "네트워크 오류가 발생했습니다."
                _eventFlow.emit(MyPageEvent.ShowToast("저장 실패: 네트워크 오류가 발생했습니다."))
            } finally {
                _tempBio.value = null
            }
        }
    }

    // 에러 메시지 초기화
    fun clearError() {
        _errorMessage.value = null
    }
}

data class MyPageState(
    val nickname: String = "임시 닉네임",
    val email: String = "ss@university.ac.kr",
    val major: String = "전공",
    val bio: String = "설명",
    val profileImageUrl: String? = null,
    val errorMessage: String? = null
)
