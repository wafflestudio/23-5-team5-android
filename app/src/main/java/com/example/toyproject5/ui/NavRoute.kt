package com.example.toyproject5.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.graphics.vector.ImageVector

sealed class NavRoute(val route: String, val title: String = "", val icon: ImageVector? = null) {
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
