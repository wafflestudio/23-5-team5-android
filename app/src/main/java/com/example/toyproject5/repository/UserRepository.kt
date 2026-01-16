package com.example.toyproject5.repository

import androidx.datastore.dataStore
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.toyproject5.data.local.UserPreferences
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody

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
    /**
     * 프로필 이미지 저장 전략 (낙관적 업데이트 기조)
     * @param uri: 로컬 또는 서버 이미지 주소
     * @param imagePart: 서버로 전송할 실제 파일 데이터 (Optional)
     */
    suspend fun saveProfileImage(uri: String, imagePart: MultipartBody.Part? = null) {
        // [선조치] 일단 전달받은 주소를 로컬 금고에 즉시 저장
        userDataStore.saveProfileImage(uri)

        // [후보고 - 서버 전송 준비]
        /*
        imagePart?.let {
            try {
                val response = userApiService.uploadProfileImage(it)
                if (response.isSuccessful) {
                    // 서버 업로드 성공 시, 서버가 준 '진짜 인터넷 주소'로 금고를 갱신
                    response.body()?.imageUrl?.let { serverUrl ->
                        userDataStore.saveProfileImage(serverUrl)
                    }
                }
            } catch (e: Exception) {
                // 서버 전송 실패 시 에러 처리 로직 (필요 시 롤백)
                e.printStackTrace()
            }
        }
        */
    }
}