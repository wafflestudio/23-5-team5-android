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
import com.example.toyproject5.ui.screens.auth.SplashScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val rootNavController = rememberNavController()

            NavHost(
                navController = rootNavController,
                startDestination = NavRoute.Splash.route
            ) {
                //  Splash 화면 추가
                composable(NavRoute.Splash.route) {
                    SplashScreen(
                        onNavigateToMain = {
                            rootNavController.navigate(NavRoute.Main.route) {
                                // 뒤로 가기 눌렀을 때 다시 Splash가 안 나오게 스택에서 제거
                                popUpTo(NavRoute.Splash.route) { inclusive = true }
                            }
                        },
                        onNavigateToLogin = {
                            rootNavController.navigate(NavRoute.Login.route) {
                                popUpTo(NavRoute.Splash.route) { inclusive = true }
                            }
                        }
                    )
                }

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