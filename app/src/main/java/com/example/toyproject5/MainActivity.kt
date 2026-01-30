package com.example.toyproject5

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.toyproject5.ui.MainScreen
import com.example.toyproject5.ui.NavRoute
import com.example.toyproject5.ui.screens.auth.LoginScreen
import com.example.toyproject5.ui.screens.auth.SignupScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val rootNavController = rememberNavController()

            // 실제 앱이라면 여기서 DataStore 등을 통해 로그인 여부를 가져옵니다.
            val isLoggedIn = false

            NavHost(
                navController = rootNavController,
                startDestination = if (isLoggedIn) NavRoute.Main.route else NavRoute.Login.route
            ) {
                // 로그인 화면
                composable(NavRoute.Login.route) {
                    LoginScreen(
                        onLoginSuccess = {
                            rootNavController.navigate(NavRoute.Main.route) {
                                popUpTo(NavRoute.Login.route) { inclusive = true }
                            }
                        },
                        onSignupClick = { rootNavController.navigate(NavRoute.Signup.route) }
                    )
                }

                // 회원가입 화면
                composable(NavRoute.Signup.route) {
                    SignupScreen(
                        onSignupComplete = { rootNavController.popBackStack() }
                    )
                }

                // 메인 화면 (기존에 작성하신 MainScreen)
                composable(NavRoute.Main.route) {
                    MainScreen(onLogout = {
                        rootNavController.navigate(NavRoute.Login.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    })
                }
            }
        }
    }
}