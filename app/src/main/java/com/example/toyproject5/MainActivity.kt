package com.example.toyproject5

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.NavType
import androidx.navigation.navArgument
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.toyproject5.ui.MainScreen
import com.example.toyproject5.ui.NavRoute
import com.example.toyproject5.ui.screens.auth.LoginScreen
import com.example.toyproject5.ui.screens.auth.SignupScreen
import com.example.toyproject5.viewmodel.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint
import com.example.toyproject5.ui.screens.auth.GoogleSignupScreen

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // 로그인 여부를 가져옴
            val isLoggedIn by authViewModel.isLoggedIn.collectAsState()
            val rootNavController = rememberNavController()

            if (isLoggedIn != null) {
                NavHost(
                    navController = rootNavController,
                    startDestination = if (isLoggedIn == true) NavRoute.Main.route else NavRoute.Login.route
                ) {
                    // 로그인 화면
                    composable(NavRoute.Login.route) {
                        LoginScreen(
                            onLoginSuccess = {
                                rootNavController.navigate(NavRoute.Main.route) {
                                    popUpTo(NavRoute.Login.route) { inclusive = true }
                                }
                            },
                            onSignupClick = { rootNavController.navigate(NavRoute.Signup.route) },
                            // registerToken과 email을 가지고 구글 전용 가입 화면으로 이동
                            onNavigateToSignup = { registerToken, email ->
                                rootNavController.navigate(NavRoute.GoogleSignup.createRoute(registerToken, email))
                            }
                        )
                    }

                    // 회원가입 화면
                    composable(NavRoute.Signup.route) {
                        SignupScreen(
                            onSignupComplete = { rootNavController.popBackStack() }
                        )
                    }

                    //구글 회원가입 화면
                    composable(
                        route = NavRoute.GoogleSignup.route,
                        arguments = listOf(
                            navArgument("token") { type = NavType.StringType },
                            navArgument("email") { type = NavType.StringType }
                        )
                    ) { backStackEntry ->
                        val token = backStackEntry.arguments?.getString("token") ?: ""
                        val email = backStackEntry.arguments?.getString("email") ?: ""

                        GoogleSignupScreen(
                            registerToken = token,
                            email = email,
                            onSignupSuccess = {
                                rootNavController.navigate(NavRoute.Main.route) {
                                    popUpTo(NavRoute.Login.route) { inclusive = true }
                                }
                            }
                        )
                    }

                    // 메인 화면
                    composable(NavRoute.Main.route) {
                        MainScreen()
                    }
                }
            }
        }
    }
}