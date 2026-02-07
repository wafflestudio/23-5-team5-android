package com.example.toyproject5.ui

import android.util.Log
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
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
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.toyproject5.viewmodel.GroupViewModel
import com.example.toyproject5.ui.screens.auth.LoginScreen
import com.example.toyproject5.ui.screens.auth.SignupScreen

@Composable
fun MainScreen(onLogout: () -> Unit) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    
    // Shared ViewModel for group-related screens to facilitate data passing
    val groupViewModel: GroupViewModel = hiltViewModel()

    // 상세 페이지 및 작성 페이지에서는 바텀바를 숨깁니다.
    val showBottomBar = currentRoute in listOf(
        NavRoute.Recruitment.route,
        NavRoute.MyJoinedGroups.route,
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
                    onPostClick = { group ->
                        groupViewModel.selectGroup(group)
                        navController.navigate(NavRoute.PostDetail.createRoute(group.id.toString()))
                    },
                    onCreatePostClick = {
                        navController.navigate(NavRoute.CreatePost.route)
                    },
                    viewModel = groupViewModel
                )
            }
            
            composable(NavRoute.MyJoinedGroups.route) {
                MyJoinedGroupsScreen(
                    onPostClick = { group ->
                        groupViewModel.selectGroup(group)
                        navController.navigate(NavRoute.PostDetail.createRoute(group.id.toString()))
                    }
                )
            }

            composable(NavRoute.MyPost.route) {
                MyPostScreen(
                    onPostClick = { groupId ->
                        groupViewModel.selectGroupById(groupId)
                        navController.navigate(NavRoute.PostDetail.createRoute(groupId.toString()))
                    },
                    onParticipantsClick = { postId ->
                        groupViewModel.selectGroupById(postId)
                        navController.navigate(NavRoute.Participants.createRoute(postId.toString()))
                    },
                    viewModel = groupViewModel
                )
            }

            composable(NavRoute.MyPage.route) {
                MyPageScreen(onNavigateToLogin = onLogout)
            }

            composable(
                route = NavRoute.PostDetail.route,
                arguments = listOf(navArgument("postId") { type = NavType.StringType })
            ) { backStackEntry ->
                val postId = backStackEntry.arguments?.getString("postId") ?: return@composable
                PostDetailScreen(
                    postId = postId, 
                    onBack = { navController.popBackStack() },
                    viewModel = groupViewModel,
                    navController = navController
                )
            }

            composable(NavRoute.CreatePost.route) {
                CreatePostScreen(onBack = { navController.popBackStack() })
            }

            composable(
                route = NavRoute.Participants.route,
                arguments = listOf(navArgument("postId") { type = NavType.StringType })
            ) { backStackEntry ->
                val postId = backStackEntry.arguments?.getString("postId") ?: return@composable
                val selectedGroup by groupViewModel.selectedGroup.collectAsState()
                ParticipantsScreen(
                    groupId = postId.toInt(),
                    groupName = selectedGroup?.groupName ?: "참여 신청자",
                    onBack = { navController.popBackStack() },
                    navController = navController
                )
            }

            composable(
                route = NavRoute.UserProfile.route,
                arguments = listOf(
                    navArgument("userId") { type = NavType.IntType }
                )
            ) { backStackEntry ->
                val userId = backStackEntry.arguments?.getInt("userId") ?: 0 // getInt로 변경
                UserProfileScreen(
                    userId = userId,
                    onBackClick = { navController.popBackStack() }
                )
            }
        }
    }
}

@Composable
fun BottomBar(navController: NavHostController) {
    val items = listOf(
        NavRoute.Recruitment,
        NavRoute.MyJoinedGroups,
        NavRoute.MyPost,
        NavRoute.MyPage
    )

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
                        popUpTo(NavRoute.Recruitment.route) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
}
