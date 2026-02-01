package com.example.toyproject5.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.toyproject5.dto.GroupCreateRequest
import com.example.toyproject5.dto.GroupResponse
import com.example.toyproject5.repository.GroupRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GroupViewModel @Inject constructor(
    private val repository: GroupRepository
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

    private var currentPage = 0
    private var isLastPage = false
    private val pageSize = 10

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
            currentPage = 0
            isLastPage = false
        } else if (isLastPage || _isMoreLoading.value) {
            return
        }

        viewModelScope.launch {
            if (isRefresh) _isLoading.value = true else _isMoreLoading.value = true
            try {
                val response = repository.searchGroups(categoryId, keyword, page = currentPage, size = pageSize)
                if (response.isSuccessful) {
                    val searchResponse = response.body()
                    val newGroups = searchResponse?.content ?: emptyList()
                    
                    if (isRefresh) {
                        _groups.value = newGroups
                    } else {
                        _groups.value = _groups.value + newGroups
                    }
                    
                    isLastPage = searchResponse?.last ?: true
                    if (!isLastPage) {
                        currentPage++
                    }
                } else {
                    _error.value = "Search failed: ${response.message()}"
                }
            } catch (e: Exception) {
                _error.value = e.localizedMessage
            } finally {
                if (isRefresh) _isLoading.value = false else _isMoreLoading.value = false
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

    fun joinGroup(groupId: Int, onSuccess: () -> Unit = {}) {
        viewModelScope.launch {
            try {
                val response = repository.joinGroup(groupId)
                if (response.isSuccessful) {
                    onSuccess()
                    fetchMyGroups()
                } else {
                    _error.value = "Failed to join group"
                }
            } catch (e: Exception) {
                _error.value = e.localizedMessage
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
