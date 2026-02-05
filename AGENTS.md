# University Student Team Recruitment Platform - Project Guide

## Project Overview
A mobile application for university students to recruit team members. Users can post and join recruitment announcements for various categories including study groups, civil service exam prep, job preparation, and extracurricular activities.

## Tech Stack

- **UI**: Jetpack Compose
- **Architecture**: MVVM (ViewModel, Repository, Coroutines)
- **Dependency Injection**: Hilt
- **Networking**: Retrofit, OkHttp
- **Asynchronous**: Kotlin Coroutines
- **Data Persistence**: Jetpack DataStore
- **Image Loading**: Coil

## File Structure

The project follows a standard Android structure with a feature-based packaging approach.

- `com.example.toyproject5`: Root package
    - `di`: Dagger Hilt modules for dependency injection.
    - `network`: Retrofit API service interfaces (`AuthApiService`, `UserApiService`, `GroupApiService`).
    - `dto`: Data Transfer Objects for API communication.
    - `repository`: Repositories that abstract data sources (`UserRepository`, `GroupRepository`).
    - `viewmodel`: ViewModels that hold and manage UI-related data (`LoginViewModel`, `MyPageViewModel`).
    - `ui`: Jetpack Compose screens and UI components.
    - `util`: Utility classes and functions.
    - `data`: Data sources, like the DataStore wrapper.
    - `MyApplication.kt`: Application class for Hilt setup.
    - `MainActivity.kt`: The single activity hosting Compose content.

## Key Features

### 1. Authentication System
- **Sign Up**: User registration.
- **Login**: Email/Password authentication.
- **Logout**: Session termination.

### 2. Group Management
- **Browse Groups**: View a list of recruitment groups.
- **Create Group**: Create a new recruitment group.
- **Join Group**: Join an existing group.

### 3. My Page
- **Profile Management**: View and edit user profile information.

## API Endpoints

Refer to `API_SPECIFICATION.md` for detailed API specifications. The following are the main service interfaces:

- `AuthApiService.kt`: Handles authentication-related API calls (login, signup).
- `UserApiService.kt`: Handles user-related API calls (profile).
- `GroupApiService.kt`: Handles group/post-related API calls (CRUD operations for groups).
- `PingApiService.kt`: For checking server connectivity.

## Notes for AI Agents

### Current Implementation Status
- The project is a native Android application built with Kotlin and Jetpack Compose.
- Core features like authentication, group browsing, and profile management are implemented.
- Hilt is used for dependency injection.
- Retrofit is used for networking.
- DataStore is used for local data persistence (e.g., auth tokens).

### When Modifying the Code
- Follow the existing MVVM architecture.
- Use Hilt for injecting dependencies.
- Use coroutines for asynchronous operations.

### Important Files to Review Before Changes
- [API_SPECIFICATION.md](./API_SPECIFICATION.md): For API details.
- `app/build.gradle.kts`: For project dependencies.
- The relevant `ApiService.kt`, `ViewModel.kt`, and `Repository.kt` files for the feature you are working on.
