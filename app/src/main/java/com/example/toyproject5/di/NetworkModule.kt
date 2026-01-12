package com.example.toyproject5.di

import com.example.toyproject5.network.PingApiService
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

@Module
@InstallIn(SingletonComponent::class) // 앱 전체에서 이 설정을 사용하겠다는 뜻
object NetworkModule {

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        // 네트워크 로그를 가로채서 보여주는 인터셉터 생성 (개발 할 때 logcat으로 서버의 응답 보기)
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        return OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("http://43.203.97.212:8080/") // ⚠️ 실제 서버 주소로 꼭 바꾸기!
            .client(provideOkHttpClient())
            // 만약 scalar (JSON 형이 아닌) 형태로 온다면, 이 부분을 추가
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun providePingApiService(retrofit: Retrofit): PingApiService {
        return retrofit.create(PingApiService::class.java)
    }
}