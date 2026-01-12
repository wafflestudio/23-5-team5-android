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
    }

    // 로컬 저장소에서 실시간으로 닉네임을 읽어옴
    val nicknameFlow: Flow<String> = dataStore.data
        .catch { exception ->
            if (exception is IOException) emit(emptyPreferences()) else throw exception
        }
        .map { preferences ->
            preferences[NICKNAME_KEY] ?: "냐냐" // 기본값 설정
        }

    // 사용자가 입력한 새로운 닉네임을 저장소에 기록함
    suspend fun saveNickname(newNickname: String) {
        dataStore.edit { preferences ->
            preferences[NICKNAME_KEY] = newNickname
        }
    }
}