package com.example.toyproject5.repository

import androidx.datastore.dataStore
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.toyproject5.data.local.UserPreferences
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class UserRepository @Inject constructor(
    private val userDataStore: UserPreferences //
) {
    // 닉네임
    // 1. 읽기: DataStore에서 흘러나오는 닉네임 흐름을 그대로 노출
    val nickname: Flow<String> = userDataStore.nicknameFlow

    // 2. 쓰기: 사용자가 닉네임을 변경했을 때 호출
    suspend fun updateNickname(newName: String) {
        // 나중에 여기에 서버 통신 코드(apiService.updateNickname)가 추가될 예정
        userDataStore.saveNickname(newName)
    }

    // 이미지
    // 1. 읽기
    val profileImageUri: Flow<String?> = userDataStore.profileImageFlow

    // 2. 쓰기: 이미지 주소를 저장소에 저장할 때 호출
    suspend fun saveProfileImage(uri: String) {
        userDataStore.saveProfileImage(uri)
    }
}