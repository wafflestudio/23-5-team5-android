package com.example.toyproject5.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.toyproject5.dto.GroupSearchResponse
import com.example.toyproject5.repository.GroupRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MyJoinedGroupsViewModel @Inject constructor(
    private val groupRepository: GroupRepository
) : ViewModel() {

    private val _myJoinedGroups = MutableStateFlow<GroupSearchResponse?>(null)
    val myJoinedGroups: StateFlow<GroupSearchResponse?> = _myJoinedGroups

    fun getMyJoinedGroups() {
        viewModelScope.launch {
            val response = groupRepository.searchJoinedGroups()
            if (response.isSuccessful) {
                _myJoinedGroups.value = response.body()
            }
        }
    }
}
