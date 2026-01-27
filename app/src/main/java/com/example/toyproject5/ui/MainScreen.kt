package com.example.toyproject5.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.toyproject5.ui.screens.*

@Composable
fun MainScreen() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // 상세 페이지 및 작성 페이지에서는 바텀바를 숨깁니다.
    val showBottomBar = currentRoute in listOf(
        NavRoute.Recruitment.route,
        NavRoute.MyPost.route,
        NavRoute.MyPage.route
    )

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                BottomBar(navController = navController)
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = NavRoute.Recruitment.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(NavRoute.Recruitment.route) {
                RecruitmentScreen(
                    onPostClick = { postId ->
                        navController.navigate(NavRoute.PostDetail.createRoute(postId.toString()))
                    },
                    onCreatePostClick = {
                        navController.navigate(NavRoute.CreatePost.route)
                    }
                )
            }
            composable(NavRoute.MyPost.route) {
                MyPostScreen(
                    onPostClick = { postId ->
                        navController.navigate(NavRoute.PostDetail.createRoute(postId.toString()))
                    },
                    onParticipantsClick = { postId ->
                        navController.navigate(NavRoute.Participants.createRoute(postId.toString()))
                    }
                )
            }
            composable(NavRoute.MyPage.route) {
                MyPageScreen()
            }
            composable(
                route = NavRoute.PostDetail.route,
                arguments = listOf(navArgument("postId") { type = NavType.StringType })
            ) { backStackEntry ->
                val postId = backStackEntry.arguments?.getString("postId") ?: return@composable
                PostDetailScreen(postId = postId, onBack = { navController.popBackStack() })
            }
            composable(NavRoute.CreatePost.route) {
                CreatePostScreen(onBack = { navController.popBackStack() })
            }
            composable(
                route = NavRoute.Participants.route,
                arguments = listOf(navArgument("postId") { type = NavType.StringType })
            ) { backStackEntry ->
                val postId = backStackEntry.arguments?.getString("postId") ?: return@composable
                ParticipantsScreen(postId = postId, onBack = { navController.popBackStack() })
            }
        }
    }
}

@Composable
fun BottomBar(navController: NavHostController) {
    val items = listOf(NavRoute.Recruitment, NavRoute.MyPost, NavRoute.MyPage)

    NavigationBar {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        items.forEach { item ->
            NavigationBarItem(
                icon = { 
                    item.icon?.let { Icon(imageVector = it, contentDescription = item.title) }
                },
                label = { Text(text = item.title) },
                selected = currentRoute == item.route,
                onClick = {
                    navController.navigate(item.route) {
                        popUpTo(navController.graph.startDestinationId) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
}
