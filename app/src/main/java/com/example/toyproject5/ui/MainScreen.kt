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
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.toyproject5.ui.screens.MyPageScreen
import com.example.toyproject5.ui.screens.MyPostScreen
import com.example.toyproject5.ui.screens.RecruitmentScreen

@Composable
fun MainScreen() {
    // 화면 이동을 담당하는 '네비게이션 컨트롤러'
    val navController = rememberNavController()

    Scaffold(
        bottomBar = {
            BottomBar(navController = navController)
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = NavRoute.Recruitment.route, // 처음 보여줄 화면
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(NavRoute.Recruitment.route) { RecruitmentScreen() }
            composable(NavRoute.MyPost.route) { MyPostScreen() }
            composable(NavRoute.MyPage.route) { MyPageScreen() }
        }
    }
}

@Composable
fun BottomBar(navController: NavHostController) {
    val items = listOf(NavRoute.Recruitment, NavRoute.MyPost, NavRoute.MyPage)

    NavigationBar {
        // 현재 내가 어떤 화면에 있는지 확인합니다.
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        items.forEach { item ->
            NavigationBarItem(
                icon = { Icon(imageVector = item.icon, contentDescription = item.title) },
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