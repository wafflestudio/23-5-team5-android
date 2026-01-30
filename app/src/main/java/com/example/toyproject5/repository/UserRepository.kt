package com.example.toyproject5.repository

import com.example.toyproject5.data.local.UserPreferences
import com.example.toyproject5.dto.LoginRequest
import com.example.toyproject5.dto.LoginResponse
import com.example.toyproject5.dto.UserMeResponse
import com.example.toyproject5.network.AuthApiService
import com.example.toyproject5.network.UserApiService
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import okhttp3.MultipartBody
import kotlin.let

class UserRepository @Inject constructor(
    private val userDataStore: UserPreferences,
    private val authApiService: AuthApiService,
    private val userApiService: UserApiService
) {
    // 닉네임
    // 1. 읽기: DataStore에서 흘러나오는 흐름을 그대로 노출
    val nickname: Flow<String> = userDataStore.nicknameFlow
    val email: Flow<String> = userDataStore.emailFlow

    // 2. 쓰기: 사용자가 닉네임을 변경했을 때 호출
    suspend fun updateNickname(newName: String) {
        // 나중에 여기에 서버 통신 코드(apiService.updateNickname)가 추가될 예정
        userDataStore.saveNickname(newName)
    }

    /**
     * 서버에서 현재 로그인된 유저의 상세 정보를 가져와 로컬 DataStore를 동기화합니다.
     */
    suspend fun fetchMyInfo(): Result<UserMeResponse> {
        return try {
            val response = userApiService.getUserMe()

            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    userDataStore.saveNickname(body.nickname)
                    userDataStore.saveEmail(body.username)
                    body.profileImageUrl?.let { userDataStore.saveProfileImage(it) }
                    // TODO: role, bio 등 추가 저장 가능

                    Result.success(body)
                } else {
                    Result.failure(Exception("응답 데이터가 비어있습니다."))
                }
            } else {
                // 401(만료) 또는 404(없음) 등의 에러 처리
                Result.failure(Exception("유저 정보를 가져오지 못했습니다. (코드: ${response.code()}, 에러명: ${response.message()})"))
            }
        } catch (e: Exception) {
            // 네트워크 연결 오류 등
            Result.failure(e)
        }
    }

    // 로그인 함수
    /**
     * [일반 로그인 함수]
     * @param loginRequest: 이메일과 비밀번호가 담긴 객체
     * @return Result<UserResponse>: 성공 또는 실패 결과를 캡슐화하여 반환
     */
    suspend fun login(loginRequest: LoginRequest): Result<LoginResponse> {
        return try {
            // 실제 서버 API 호출
            val response = authApiService.login(loginRequest)

            // 서버 응답 결과 확인
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    // 성공 시 토큰과 닉네임을 로컬 저장소(DataStore)에 저장
                    userDataStore.saveNickname(body.nickname)
                    userDataStore.saveToken(body.accessToken)

                    // 이메일은 loginRequest에 있었음
                    userDataStore.saveEmail(loginRequest.email)

                    // 성공 결과 반환
                    Result.success(body)
                } else {
                    Result.failure(Exception("서버 응답 데이터가 없습니다."))
                }
            } else {
                // 서버 에러 처리
                Result.failure(Exception("로그인 실패 (에러 코드: ${response.code()})"))
            }
        } catch (e: Exception) {
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