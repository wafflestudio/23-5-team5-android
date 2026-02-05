package com.example.toyproject5.repository

import com.example.toyproject5.data.local.UserPreferences
import com.example.toyproject5.dto.EmailConfirmRequest
import com.example.toyproject5.dto.EmailVerificationRequest
import com.example.toyproject5.dto.SocialLoginRequest
import com.example.toyproject5.dto.LoginRequest
import com.example.toyproject5.dto.LoginResponse
import com.example.toyproject5.dto.SignupRequest
import com.example.toyproject5.dto.UserMeResponse
import com.example.toyproject5.dto.SocialLoginResponse
import com.example.toyproject5.dto.SocialSignupRequest
import com.example.toyproject5.dto.SignupResponse
import com.example.toyproject5.dto.SocialVerifyRequest
import com.example.toyproject5.network.AuthApiService
import com.example.toyproject5.network.UserApiService
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import okhttp3.MultipartBody
import org.json.JSONObject
import kotlin.let

class UserRepository @Inject constructor(
    private val userDataStore: UserPreferences,
    private val userApiService: UserApiService
) {
    // 개인정보
    // 읽기: DataStore에서 흘러나오는 흐름을 그대로 노출
    val nickname: Flow<String> = userDataStore.nicknameFlow
    val email: Flow<String> = userDataStore.emailFlow
    val major: Flow<String?> = userDataStore.majorFlow
    val bio: Flow<String?> = userDataStore.bioFlow

    // 쓰기: 사용자가 닉네임을 변경했을 때 호출
    suspend fun updateNickname(newName: String): Result<UserMeResponse> {
        return updateProfileInfo(nickname = newName)
    }

    // 전공 변경
    suspend fun updateMajor(newMajor: String): Result<UserMeResponse> {
        return updateProfileInfo(major = newMajor)
    }

    // 자기소개 변경
    suspend fun updateBio(newBio: String): Result<UserMeResponse> {
        return updateProfileInfo(bio = newBio)
    }

    // 유저 정보 변경
    suspend fun updateProfileInfo(
        nickname: String? = null,
        major: String? = null,
        bio: String? = null
    ): Result<UserMeResponse> {
        return try {
            val nicknamePart = nickname?.let {
                MultipartBody.Part.createFormData("nickname", it)
            }
            val majorPart = major?.let {
                MultipartBody.Part.createFormData("major", it)
            }
            val bioPart = bio?.let {
                MultipartBody.Part.createFormData("bio", it)
            }

            val response = userApiService.updateProfileText(
                nickname = nicknamePart,
                major = majorPart,
                bio = bioPart
            )

            if (response.isSuccessful && response.body() != null) {
                val updatedData = response.body()!!

                userDataStore.saveNickname(updatedData.nickname)
                updatedData.major?.let { userDataStore.saveMajor(it) }
                updatedData.bio?.let { userDataStore.saveBio(it) }
                updatedData.profileImageUrl?.let { userDataStore.saveProfileImage(it) }

                Result.success(updatedData)
            } else {
                val errorMessage = when (response.code()) {
                    409 -> "이미 사용 중인 닉네임입니다."
                    400 -> "입력값이 유효하지 않습니다."
                    else -> "프로필 수정 실패 (에러 코드: ${response.code()})"
                }
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            // 네트워크 연결 실패 등 예외 처리
            Result.failure(e)
        }
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
        imagePart?.let {
            try {
                val response = userApiService.uploadProfileImage(it)
                if (response.isSuccessful) {
                    // 서버 업로드 성공 시, 서버가 준 '진짜 인터넷 주소'로 금고를 갱신
                    response.body()?.profileImageUrl?.let { serverUrl ->
                        userDataStore.saveProfileImage(serverUrl)
                    }
                }
            } catch (e: Exception) {
                // 서버 전송 실패 시 에러 처리 로직 (필요 시 롤백)
                e.printStackTrace()
            }
        }
    }
}