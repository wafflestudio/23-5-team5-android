package com.example.toyproject5.ui.screens.auth

data class SignupUiState(
    val currentStep: Int = 1,
    val isLoading: Boolean = false,
    val isEmailSent: Boolean = false,
    val isVerified: Boolean = false,
    val isSignupSuccess: Boolean = false,
    val errorMessage: String? = null
)