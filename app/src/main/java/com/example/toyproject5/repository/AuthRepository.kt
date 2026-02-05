package com.example.toyproject5.repository

import com.example.toyproject5.data.local.UserPreferences
import com.example.toyproject5.dto.EmailConfirmRequest
import com.example.toyproject5.dto.EmailVerificationRequest
import com.example.toyproject5.dto.LoginRequest
import com.example.toyproject5.dto.LoginResponse
import com.example.toyproject5.dto.SignupRequest
import com.example.toyproject5.dto.SignupResponse
import com.example.toyproject5.dto.SocialLoginRequest
import com.example.toyproject5.dto.SocialLoginResponse
import com.example.toyproject5.dto.SocialSignupRequest
import com.example.toyproject5.dto.SocialVerifyRequest
import com.example.toyproject5.dto.UserMeResponse
import com.example.toyproject5.network.AuthApiService
import com.example.toyproject5.network.UserApiService
import kotlinx.coroutines.flow.first
import org.json.JSONObject
import javax.inject.Inject
import kotlin.let

class AuthRepository @Inject constructor(
    private val userDataStore: UserPreferences,
    private val authApiService: AuthApiService,
    private val userApiService: UserApiService
)  {

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
            if (response.isSuccessful) {
                Result.success(Unit)
            }
            else {
                val errorBodyString = response.errorBody()?.string()

                val errorMessage = errorBodyString?.let {
                    try {
                        JSONObject(it).getString("message")
                    } catch (e: Exception) {
                        null
                    }
                } ?: "인증 발송 중 오류가 발생했습니다. (Error Code: ${response.code()})"

                Result.failure(Exception(errorMessage))
            }
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
                } ?: "인증 코드 확인 중 오류가 발생했습니다. (Error Code: ${response.code()})"

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
                } ?: "회원가입 중 오류가 발생했습니다. (Error Code: ${response.code()})"

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
                val errorBodyString = response.errorBody()?.string()

                val errorMessage = errorBodyString?.let {
                    try {
                        JSONObject(it).getString("message")
                    } catch (e: Exception) {
                        null
                    }
                } ?: "구글 로그인 중 오류가 발생했습니다. (Error Code: ${response.code()})"

                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * [구글 회원가입 재학생 인증]
     * @param registerToken: 구글에서 받은 회원가입용 token
     * @param snuEmail: 재학생 인증 이메일
     * @param code: 인증 코드
     */
    suspend fun verifySocialEmailCode(registerToken: String, snuEmail: String, code: String): Result<SocialLoginResponse> {
        return try {
            // 소셜 전용 인증 API 호출
            val response = authApiService.verifySocialEmailCode(
                SocialVerifyRequest(
                    registerToken,
                    snuEmail,
                    code
                )
            )
            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                if (body.type == "REGISTER") {
                    // 케이스 A: 추가 회원가입이 필요한 상태
                    // 이메일만 저장하고 회원가입 화면으로
                    userDataStore.saveEmail(snuEmail)
                    Result.success(body)
                } else {
                    // 케이스 B: 이미 가입된 유저 (로그인 성공)
                    userDataStore.saveEmail(snuEmail)
                    userDataStore.saveToken(body.token) // 최종 토큰 저장
                    Result.success(body)
                }
            }
            else {
                val errorBodyString = response.errorBody()?.string()

                val errorMessage = errorBodyString?.let {
                    try {
                        JSONObject(it).getString("message")
                    } catch (e: Exception) {
                        null
                    }
                } ?: "재학생 인증 확인 중 오류가 발생했습니다. (Error Code: ${response.code()})"

                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) { Result.failure(e) }
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
                val errorBodyString = response.errorBody()?.string()

                val errorMessage = errorBodyString?.let {
                    try {
                        JSONObject(it).getString("message")
                    } catch (e: Exception) {
                        null
                    }
                } ?: "구글 회원가입 중 오류가 발생했습니다. (Error Code: ${response.code()})"

                Result.failure(Exception(errorMessage))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
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

}