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
    private val authApiService: AuthApiService,
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

    // 정보 불러오기
    suspend fun fetchMyInfo(): Result<UserMeResponse> {
        return try {
            val response = userApiService.getUserMe()

            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    userDataStore.saveNickname(body.nickname)
                    body.profileImageUrl?.let { userDataStore.saveProfileImage(it) }
                    body.major?.let { userDataStore.saveMajor(it) }
                    body.bio?.let { userDataStore.saveBio(it) }

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

                // TODO: 현재 예시에서는 닉네임만 저장하고 있지만, major 등도 저장
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
                    // 성공 시 토큰 로컬 저장소(DataStore)에 저장
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

    // 이메일 인증 발송
    suspend fun sendEmail(email: String): Result<Unit> {
        return try {
            val response = authApiService.sendVerificationEmail(EmailVerificationRequest(email))
            if (response.isSuccessful) Result.success(Unit)
            else Result.failure(Exception("메일 발송 실패"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // 인증 코드 확인
    suspend fun verifyCode(email: String, code: String): Result<Unit> {
        return try {
            val response = authApiService.verifyEmailCode(EmailConfirmRequest(email, code))
            if (response.isSuccessful) {
                Result.success(Unit)
            } else {
                val errorBodyString = response.errorBody()?.string()

                val errorMessage = errorBodyString?.let {
                    try {
                        JSONObject(it).getString("message")
                    } catch (e: Exception) {
                        null
                    }
                } ?: "인증 확인 중 오류가 발생했습니다."

                // 실제 서버 메시지를 Exception에 담아 반환
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // 회원가입
    suspend fun signup(request: SignupRequest): Result<SignupResponse> {
        return try {
            val response = authApiService.signup(request)
            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                // 가입 성공 시 토큰 저장
                userDataStore.saveToken(body.accessToken)
                Result.success(body)
            } else {
                val errorBodyString = response.errorBody()?.string()

                val errorMessage = errorBodyString?.let {
                    try {
                        JSONObject(it).getString("message")
                    } catch (e: Exception) {
                        null
                    }
                } ?: "회원가입 중 오류가 발생했습니다."

                // 실제 서버 메시지를 Exception에 담아 반환
                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * [구글 로그인 처리]
     * @param idToken: 구글에서 받은 id_token
     * @param email: 로컬에 저장할 구글 이메일
     */
    suspend fun handleGoogleAuth(idToken: String, email: String): Result<SocialLoginResponse> {
        return try {
            val response = authApiService.googleLogin(googleLoginRequest = SocialLoginRequest(idToken))

            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!

                if (body.type == "REGISTER") {
                    // 케이스 A: 추가 회원가입이 필요한 상태
                    // 이메일만 저장하고 회원가입 화면으로 보내야 합니다.
                    userDataStore.saveEmail(email)
                    Result.success(body)
                } else {
                    // 케이스 B: 이미 가입된 유저 (로그인 성공)
                    userDataStore.saveEmail(email)
                    userDataStore.saveToken(body.token) // 최종 토큰 저장
                    // 필요하다면 닉네임 등 추가 정보 저장
                    Result.success(body)
                }
            } else {
                Result.failure(Exception("인증 실패: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * [구글 회원가입 처리]
     * @param request: 서버가 요구하는 추가 정보 (임시 토큰, 학과, 학번, 닉네임 등)
     * @return Result<SocialSignupResponse>: 최종 가입 및 로그인 성공 결과
     */
    suspend fun googleSignup(request: SocialSignupRequest): Result<SignupResponse> {
        return try {
            // 소셜 회원가입 API 호출
            val response = authApiService.googleSignup(provider = "google", request = request)

            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!

                // 가입 성공 시 최종 액세스 토큰과 닉네임 저장
                userDataStore.saveToken(body.accessToken)
                userDataStore.saveNickname(body.nickname)

                Result.success(body)
            } else {
                Result.failure(Exception("회원가입 실패 (에러 코드: ${response.code()})"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * [자동 로그인 함수]
     * 1. 로컬 DataStore에서 토큰을 꺼낸다.
     * 2. 토큰이 없다면 바로 실패(false) 반환.
     * 3. 토큰이 있다면 서버에 '내 정보'를 요청(fetchMyInfo)하여 토큰의 유효성을 검증한다.
     */
    suspend fun checkAutoLogin(): Boolean {
        val token = userDataStore.tokenFlow.first()

        if (token.isNullOrBlank()) {
            return false // 토큰이 없으므로 수동 로그인 필요
        }

        val result = fetchMyInfo()
        return result.isSuccess
    }

    // 로그아웃
    suspend fun logout() {
        userDataStore.clearUserData()
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