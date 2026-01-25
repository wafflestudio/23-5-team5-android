package com.example.toyproject5.data.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

class UserPreferences @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {
    private companion object {
        val NICKNAME_KEY = stringPreferencesKey("user_nickname")
        val PROFILE_IMAGE_KEY = stringPreferencesKey("profile_image_uri")
        val TOKEN_KEY = stringPreferencesKey("user_token")
        val EMAIL_KEY = stringPreferencesKey("user_email")
    }

    // 로컬 저장소에서 실시간으로 닉네임을 읽어옴
    val nicknameFlow: Flow<String> = dataStore.data
        .catch { exception ->
            if (exception is IOException) emit(emptyPreferences()) else throw exception
        }
        .map { preferences ->
            preferences[NICKNAME_KEY] ?: "냐냐" // 기본값 설정
        }

    // 로컬 저장소에서 실시간으로 이메일을 읽어옴
    val emailFlow: Flow<String> = dataStore.data
        .catch { exception ->
            if (exception is IOException) emit(emptyPreferences()) else throw exception
        }
        .map { preferences ->
            preferences[EMAIL_KEY] ?: "ss.university.ac.kr" // 기본값 설정
        }

    // 로컬 저장소에서 실시간으로 프로필 이미지을 읽어옴
    val profileImageFlow: Flow<String?> = dataStore.data
        .catch { exception ->
            if (exception is IOException) emit(emptyPreferences()) else throw exception
        }
        .map { preferences ->
            // 금고에서 이미지 주소를 꺼냅니다. 없으면 null
            preferences[PROFILE_IMAGE_KEY]
        }

    // 사용자가 입력한 새로운 닉네임을 저장소에 기록함
    suspend fun saveNickname(newNickname: String) {
        dataStore.edit { preferences ->
            preferences[NICKNAME_KEY] = newNickname
        }
    }

    // 사용자가 입력한 이메일을 저장소에 기록. (현재 로직으로는 이메일이 수정될 일은 없음.)
    suspend fun saveEmail(email: String) {
        dataStore.edit { preferences ->
            preferences[EMAIL_KEY] = email
        }
    }

    // 사용자가 선택한 프로필 이미지을 저장소에 기록함
    suspend fun saveProfileImage(uri: String) {
        dataStore.edit { preferences ->
            preferences[PROFILE_IMAGE_KEY] = uri
        }
    }

    // 토큰 저장용
    suspend fun saveToken(token: String) {
        dataStore.edit { preferences ->
            preferences[TOKEN_KEY] = token
        }
    }
}