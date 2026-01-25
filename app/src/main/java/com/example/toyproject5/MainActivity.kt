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
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.toyproject5.ui.MainScreen
import com.example.toyproject5.ui.NavRoute
import com.example.toyproject5.ui.screens.auth.LoginScreen
import com.example.toyproject5.ui.screens.auth.SignupScreen
import com.example.toyproject5.viewmodel.AuthViewModel
import dagger.hilt.android.AndroidEntryPoint

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
                        MainScreen()
                    }
                }
            }
        }
    }
}