package com.example.toyproject5.di

import com.example.toyproject5.network.AuthApiService
import com.example.toyproject5.network.AuthInterceptor
import com.example.toyproject5.network.PingApiService
import com.example.toyproject5.network.UserApiService
import com.example.toyproject5.network.GroupApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import javax.inject.Singleton
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import javax.inject.Named
import kotlin.jvm.java

@Module
@InstallIn(SingletonComponent::class) // 앱 전체에서 이 설정을 사용하겠다는 뜻
object NetworkModule {

    private const val BASE_URL = "http://43.203.97.212:8080/"


    // 공통 로그 인터셉터
    @Provides
    @Singleton
    fun provideLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }

    // 로그인, 회원가입 전용 OkHttpClient - 토큰 인터셉터가 없음.
    @Provides
    @Singleton
    @Named("AuthClient")
    fun provideAuthOkHttpClient(
        loggingInterceptor: HttpLoggingInterceptor
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .build()
    }

    // 로그인, 회원가입 전용 Retrofit
    @Provides
    @Singleton
    @Named("AuthRetrofit")
    fun provideAuthRetrofit(
        @Named("AuthClient") okHttpClient: OkHttpClient
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    // 일반 API 전용 - 토큰 인터셉터 필요.
    @Provides
    @Singleton
    @Named("DefaultClient")
    fun provideDefaultOkHttpClient(
        loggingInterceptor: HttpLoggingInterceptor,
        authInterceptor: AuthInterceptor
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor(authInterceptor) // 토큰 추가
            .build()
    }

    // 일반 API 전용 Retrofit
    @Provides
    @Singleton
    @Named("DefaultRetrofit")
    fun provideDefaultRetrofit(
        @Named("DefaultClient") okHttpClient: OkHttpClient // DefaultClient를 쓰라고 지정
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun providePingApiService(@Named("AuthRetrofit") retrofit: Retrofit): PingApiService {
        return retrofit.create(PingApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideUserApiService(@Named("DefaultRetrofit") retrofit: Retrofit): UserApiService {
        return retrofit.create(UserApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideAuthApiService(@Named("AuthRetrofit") retrofit: Retrofit): AuthApiService {
        return retrofit.create(AuthApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideGroupApiService(@Named("DefaultRetrofit") retrofit: Retrofit): GroupApiService {
        return retrofit.create(GroupApiService::class.java)
    }
}