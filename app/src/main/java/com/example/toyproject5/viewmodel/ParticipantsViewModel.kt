package com.example.toyproject5.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.toyproject5.dto.UserSearchResponseDto
import com.example.toyproject5.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ParticipantsViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _participants = MutableStateFlow<List<UserSearchResponseDto>>(emptyList())
    val participants: StateFlow<List<UserSearchResponseDto>> = _participants

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun fetchParticipants(groupId: Int) {
        viewModelScope.launch {
            val result = userRepository.getParticipants(groupId)
            result.onSuccess {
                _participants.value = it
            }.onFailure {
                _error.value = it.message
            }
        }
    }
}
