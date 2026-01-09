package com.example.toyproject5.di

import com.example.toyproject5.network.PingApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@Module
@InstallIn(SingletonComponent::class) // 앱 전체에서 이 설정을 사용하겠다는 뜻
object NetworkModule {

    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://your-server-url.com/") // ⚠️ 실제 서버 주소로 꼭 바꾸기!
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun providePingApiService(retrofit: Retrofit): PingApiService {
        return retrofit.create(PingApiService::class.java)
    }
}