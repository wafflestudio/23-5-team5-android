package com.example.toyproject5.ui.screens.auth

data class SocialVerifyUiState(
    val currentStep: Int = 1,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isVerificationSuccess: Boolean = false,
    val verifiedEmail: String? = null
)