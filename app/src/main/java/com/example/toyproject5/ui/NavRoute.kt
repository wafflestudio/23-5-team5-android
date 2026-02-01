package com.example.toyproject5.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.graphics.vector.ImageVector

sealed class NavRoute(val route: String, val title: String = "", val icon: ImageVector? = null) {

    // 자동 로그인 기능 및 앱의 시작 화면 (Splash)
    object Splash : NavRoute("splash")

    object Login : NavRoute("login", "로그인", Icons.Default.Home)

    object Signup : NavRoute("signup", "회원가입", Icons.Default.List) // 일반 회원가입

    // 구글 회원가입
    object GoogleSignup : NavRoute("google_signup/{token}/{email}") {
        fun createRoute(token: String, email: String) = "google_signup/$token/$email"
    }

    // Main Route (MainScreen)
    object Main : NavRoute("main", "메인", Icons.Default.Home)


    object Recruitment : NavRoute("recruitment", "모집공고", Icons.Default.Home)
    object MyPost : NavRoute("mypost", "내 공고", Icons.Default.List)
    object MyPage : NavRoute("mypage", "마이페이지", Icons.Default.Person)
    
    object PostDetail : NavRoute("postDetail/{postId}") {
        fun createRoute(postId: String) = "postDetail/$postId"
    }

    object CreatePost : NavRoute("createPost")

    object Participants : NavRoute("participants/{postId}") {
        fun createRoute(postId: String) = "participants/$postId"
    }
}