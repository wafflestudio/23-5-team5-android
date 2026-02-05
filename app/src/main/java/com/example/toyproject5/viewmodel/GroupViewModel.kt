package com.example.toyproject5.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.toyproject5.dto.ErrorResponse
import com.example.toyproject5.dto.GroupCreateRequest
import com.example.toyproject5.dto.GroupResponse
import com.example.toyproject5.repository.GroupRepository
import com.example.toyproject5.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GroupViewModel @Inject constructor(
    private val repository: GroupRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _groups = MutableStateFlow<List<GroupResponse>>(emptyList())
    val groups: StateFlow<List<GroupResponse>> = _groups.asStateFlow()

    private val _myGroups = MutableStateFlow<List<GroupResponse>>(emptyList())
    val myGroups: StateFlow<List<GroupResponse>> = _myGroups.asStateFlow()

    private val _selectedGroup = MutableStateFlow<GroupResponse?>(null)
    val selectedGroup: StateFlow<GroupResponse?> = _selectedGroup.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _isMoreLoading = MutableStateFlow(false)
    val isMoreLoading: StateFlow<Boolean> = _isMoreLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    val currentUserId: StateFlow<Long?> = userRepository.userId
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    private var currentPage = 0
    private var isLastPage = false
    private val pageSize = 10
    private var searchJob: Job? = null

    fun selectGroup(group: GroupResponse) {
        _selectedGroup.value = group
    }

    fun selectGroupById(groupId: Int) {
        // Search in already fetched lists
        val group = _groups.value.find { it.id == groupId } ?: _myGroups.value.find { it.id == groupId }
        if (group != null) {
            _selectedGroup.value = group
        }
    }

    fun createGroup(request: GroupCreateRequest, onSuccess: () -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = repository.createGroup(request)
                if (response.isSuccessful) {
                    onSuccess()
                } else {
                    _error.value = "Failed to create group: ${response.message()}"
                }
            } catch (e: Exception) {
                _error.value = e.localizedMessage
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun searchGroups(categoryId: Int? = null, keyword: String? = null, isRefresh: Boolean = true) {
        if (isRefresh) {
            searchJob?.cancel()
            currentPage = 0
            isLastPage = false
            _isLoading.value = true
        } else {
            if (isLastPage || _isMoreLoading.value || _isLoading.value) {
                return
            }
            _isMoreLoading.value = true
        }

        searchJob = viewModelScope.launch {
            try {
                val response = repository.searchGroups(categoryId, keyword, page = currentPage, size = pageSize)
                if (response.isSuccessful) {
                    val searchResponse = response.body()
                    val newGroups = searchResponse?.content ?: emptyList()
                    
                    if (isRefresh) {
                        _groups.value = newGroups
                    } else {
                        // Prevent duplicates by checking IDs
                        val currentList = _groups.value
                        val filteredNewGroups = newGroups.filter { newItem ->
                            currentList.none { it.id == newItem.id }
                        }
                        _groups.value = currentList + filteredNewGroups
                    }
                    
                    isLastPage = searchResponse?.last ?: true
                    if (!isLastPage) {
                        currentPage++
                    }
                } else {
                    _error.value = "Search failed: ${response.message()}"
                }
            } catch (e: Exception) {
                if (e !is kotlinx.coroutines.CancellationException) {
                    _error.value = e.localizedMessage
                }
            } finally {
                if (isRefresh) {
                    _isLoading.value = false
                } else {
                    _isMoreLoading.value = false
                }
            }
        }
    }

    fun fetchMyGroups() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = repository.searchMyGroups()
                if (response.isSuccessful) {
                    _myGroups.value = response.body()?.content ?: emptyList()
                } else {
                    _error.value = "Failed to fetch my groups"
                }
            } catch (e: Exception) {
                _error.value = e.localizedMessage
            } finally {
                _isLoading.value = false
            }
        }
    }

    // [추가] Toast 메시지처럼 일회성 이벤트를 위한 Flow입니다.
    private val _toastEvent = MutableSharedFlow<String>()
    val toastEvent: SharedFlow<String> = _toastEvent.asSharedFlow()

    fun joinGroup(groupId: Int, onSuccess: () -> Unit = {}) {
        viewModelScope.launch {
            try {
                val response = repository.joinGroup(groupId)
                if (response.isSuccessful) {
                    _toastEvent.emit("참여 신청이 완료되었습니다.")
                    onSuccess()
                    fetchMyGroups()
                } else {
                    val errorBodyString = response.errorBody()?.string()

                    val finalMessage = if (!errorBodyString.isNullOrBlank()) {
                        try {
                            // Gson을 이용해 JSON 문자열을 ErrorResponse 객체로 변환
                            val gson = com.google.gson.Gson()
                            val errorData = gson.fromJson(errorBodyString, ErrorResponse::class.java)
                            errorData.message // "이미 가입한 그룹입니다." 추출
                        } catch (e: Exception) {
                            "에러 메시지 분석 중 오류가 발생했습니다."
                        }
                    } else {
                        "가입 실패 (코드: ${response.code()})"
                    }

                    _toastEvent.emit(finalMessage)
                }
            } catch (e: Exception) {
                _error.value = e.localizedMessage
                _toastEvent.emit("네트워크 오류가 발생했습니다: ${e.localizedMessage}")
            }
        }
    }

    fun withdrawFromGroup(groupId: Int) {
        viewModelScope.launch {
            try {
                val response = repository.withdrawFromGroup(groupId)
                if (response.isSuccessful) {
                    fetchMyGroups()
                } else {
                    _error.value = "Failed to withdraw from group"
                }
            } catch (e: Exception) {
                _error.value = e.localizedMessage
            }
        }
    }

    fun expireGroup(groupId: Int) {
        viewModelScope.launch {
            try {
                val response = repository.expireGroup(groupId)
                if (response.isSuccessful) {
                    fetchMyGroups()
                } else {
                    _error.value = "Failed to expire group"
                }
            } catch (e: Exception) {
                _error.value = e.localizedMessage
            }
        }
    }

    fun deleteGroup(groupId: Int) {
        viewModelScope.launch {
            try {
                val response = repository.deleteGroup(groupId)
                if (response.isSuccessful) {
                    fetchMyGroups()
                } else {
                    _error.value = "Failed to delete group"
                }
            } catch (e: Exception) {
                _error.value = e.localizedMessage
            }
        }
    }

    fun clearError() {
        _error.value = null
    }
}
