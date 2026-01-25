# Study Group API Specification

## Base Information

- **Base URL**: `https://toyproject-team5.kro.kr/api`
- **Version**: 1.0
- **Content-Type**: `application/json` (except for file upload endpoints)
- **Date**: 2026-01-25

---

## Table of Contents

1. [Authentication](#authentication)
2. [Health Check](#health-check)
3. [User Management](#user-management)
4. [OAuth](#oauth)
5. [Email Verification](#email-verification)
6. [Group Management](#group-management)
7. [User-Group Operations](#user-group-operations)
8. [Search](#search)
9. [Error Handling](#error-handling)

---

## Authentication

Most endpoints require JWT authentication. Include the access token in the Authorization header:

```
Authorization: Bearer <access_token>
```

---

## Health Check

### Ping Server

Check if the server is running.

- **Endpoint**: `GET /ping`
- **Authentication**: Not required
- **Response**: `200 OK`

```
pong
```

---

## User Management

### 1. Sign Up

Register a new user account.

- **Endpoint**: `POST /auth/signup`
- **Authentication**: Not required
- **Content-Type**: `application/json`

#### Request Body

```json
{
  "username": "string",
  "password": "string",
  "major": "string",
  "student_number": "string",
  "nickname": "string"
}
```

| Field          | Type   | Required | Description                   |
| -------------- | ------ | -------- | ----------------------------- |
| username       | string | Yes      | User's username               |
| password       | string | Yes      | User's password               |
| major          | string | Yes      | User's major/department       |
| student_number | string | Yes      | Student identification number |
| nickname       | string | Yes      | User's nickname               |

#### Response: `200 OK`

```json
{
  "accessToken": "string",
  "username": "string",
  "nickname": "string",
  "isVerified": false
}
```

---

### 2. Login

Authenticate an existing user.

- **Endpoint**: `POST /auth/login`
- **Authentication**: Not required
- **Content-Type**: `application/json`

#### Request Body

```json
{
  "username": "string",
  "password": "string"
}
```

| Field    | Type   | Required | Description     |
| -------- | ------ | -------- | --------------- |
| username | string | Yes      | User's username |
| password | string | Yes      | User's password |

#### Response: `200 OK`

```json
{
  "accessToken": "string",
  "nickname": "string",
  "isVerified": true
}
```

---

### 3. Get Profile

Retrieve the authenticated user's profile information.

- **Endpoint**: `GET /users/me`
- **Authentication**: Required (JWT)
- **Content-Type**: `application/json`

#### Response: `200 OK`

```json
{
  "userId": 1,
  "username": "string",
  "major": "string",
  "studentNumber": "string",
  "nickname": "string",
  "profileImageUrl": "https://...",
  "bio": "string",
  "role": "USER",
  "createdAt": "2026-01-25T10:30:00Z"
}
```

---

### 4. Update Profile

Update the authenticated user's profile information.

- **Endpoint**: `PATCH /users/me`
- **Authentication**: Required (JWT)
- **Content-Type**: `multipart/form-data`

#### Request Body (Form Data)

| Field         | Type   | Required | Description              |
| ------------- | ------ | -------- | ------------------------ |
| major         | string | No       | Updated major/department |
| nickname      | string | No       | Updated nickname         |
| profile_image | file   | No       | Profile image file       |
| bio           | string | No       | User bio/description     |

#### Response: `200 OK`

```json
{
  "userId": 1,
  "username": "string",
  "major": "string",
  "studentNumber": "string",
  "nickname": "string",
  "profileImageUrl": "https://...",
  "bio": "string",
  "role": "USER",
  "createdAt": "2026-01-25T10:30:00Z"
}
```

---

### 5. Update Profile Image

Update only the profile image.

- **Endpoint**: `PUT /users/me/profile-image`
- **Authentication**: Required (JWT)
- **Content-Type**: `multipart/form-data`

#### Request Body (Form Data)

| Field         | Type | Required | Description            |
| ------------- | ---- | -------- | ---------------------- |
| profile_image | file | Yes      | New profile image file |

#### Response: `200 OK`

```json
{
  "userId": 1,
  "username": "string",
  "major": "string",
  "studentNumber": "string",
  "nickname": "string",
  "profileImageUrl": "https://...",
  "bio": "string",
  "role": "USER",
  "createdAt": "2026-01-25T10:30:00Z"
}
```

---

## OAuth

### Social Login (Google)

Authenticate using Google OAuth.

- **Endpoint**: `POST /oauth/login/{provider}`
- **Authentication**: Not required
- **Content-Type**: `application/json`

#### Path Parameters

| Parameter | Type   | Description                                   |
| --------- | ------ | --------------------------------------------- |
| provider  | string | OAuth provider (currently supports: `google`) |

#### Request Body

```json
{
  "token": "string"
}
```

| Field | Type   | Required | Description                     |
| ----- | ------ | -------- | ------------------------------- |
| token | string | Yes      | Google ID token from OAuth flow |

#### Response: `200 OK`

```json
{
  "type": "LOGIN",
  "token": "string"
}
```

| Field | Description                                                     |
| ----- | --------------------------------------------------------------- |
| type  | Either `"LOGIN"` (existing user) or `"REGISTER"` (new user)     |
| token | AccessToken (if type=LOGIN) or RegisterToken (if type=REGISTER) |

**Note**: If `type` is `"REGISTER"`, use the returned token for email verification flow.

---

## Email Verification

### 1. Send Verification Code

Send a verification code to the user's email.

- **Endpoint**: `POST /auth/code`
- **Authentication**: Not required
- **Content-Type**: `application/json`

#### Request Body

```json
{
  "email": "user@example.com"
}
```

| Field | Type   | Required | Description                             |
| ----- | ------ | -------- | --------------------------------------- |
| email | string | Yes      | Email address to send verification code |

#### Response: `200 OK`

```
인증번호가 발송되었습니다. (유효시간 3분)
```

---

### 2. Verify Email Code

Verify the email with the code sent.

- **Endpoint**: `POST /auth/verify`
- **Authentication**: Not required (but requires register_token)
- **Content-Type**: `application/json`

#### Request Body

```json
{
  "register_token": "string",
  "email": "user@example.com",
  "code": "123456"
}
```

| Field          | Type   | Required | Description                                  |
| -------------- | ------ | -------- | -------------------------------------------- |
| register_token | string | Yes      | Registration token from OAuth login response |
| email          | string | Yes      | Email address being verified                 |
| code           | string | Yes      | Verification code from email                 |

#### Response: `200 OK`

```json
{
  "type": "LOGIN",
  "token": "string"
}
```

---

## Group Management

### 1. Create Group

Create a new study group.

- **Endpoint**: `POST /groups`
- **Authentication**: Required (JWT)
- **Content-Type**: `application/json`

#### Request Body

```json
{
  "group_name": "string",
  "description": "string",
  "category_id": 1,
  "sub_category_id": 1,
  "capacity": 10,
  "is_online": true,
  "location": "string"
}
```

| Field           | Type    | Required | Description                                     |
| --------------- | ------- | -------- | ----------------------------------------------- |
| group_name      | string  | Yes      | Name of the study group                         |
| description     | string  | Yes      | Group description                               |
| category_id     | number  | Yes      | Main category ID                                |
| sub_category_id | number  | Yes      | Sub-category ID                                 |
| capacity        | number  | No       | Maximum number of members (null = unlimited)    |
| is_online       | boolean | Yes      | Whether the group is online or offline          |
| location        | string  | Yes      | Location (for offline) or platform (for online) |

#### Response: `200 OK`

```
(Empty response body)
```

---

### 2. Delete Group

Delete a study group. Only the group leader can delete.

- **Endpoint**: `DELETE /groups`
- **Authentication**: Required (JWT)
- **Content-Type**: `application/json`

#### Request Body

```json
{
  "group_id": 1
}
```

| Field    | Type   | Required | Description               |
| -------- | ------ | -------- | ------------------------- |
| group_id | number | Yes      | ID of the group to delete |

#### Response: `200 OK`

```
(Empty response body)
```

---

### 3. Expire Group

Mark a group as expired (close recruitment). Only the group leader can expire.

- **Endpoint**: `PATCH /groups/expire`
- **Authentication**: Required (JWT)
- **Content-Type**: `application/json`

#### Request Body

```json
{
  "group_id": 1
}
```

| Field    | Type   | Required | Description               |
| -------- | ------ | -------- | ------------------------- |
| group_id | number | Yes      | ID of the group to expire |

#### Response: `200 OK`

```
(Empty response body)
```

---

## User-Group Operations

### 1. Join Group

Join an existing study group.

- **Endpoint**: `POST /groups/join`
- **Authentication**: Required (JWT)
- **Content-Type**: `application/json`

#### Request Body

```json
{
  "groupId": 1
}
```

| Field   | Type   | Required | Description             |
| ------- | ------ | -------- | ----------------------- |
| groupId | number | Yes      | ID of the group to join |

#### Response: `200 OK`

```
(Empty response body)
```

---

### 2. Withdraw from Group

Leave a study group.

- **Endpoint**: `DELETE /groups/join`
- **Authentication**: Required (JWT)
- **Content-Type**: `application/json`

#### Request Body

```json
{
  "groupId": 1
}
```

| Field   | Type   | Required | Description              |
| ------- | ------ | -------- | ------------------------ |
| groupId | number | Yes      | ID of the group to leave |

#### Response: `200 OK`

```
(Empty response body)
```

---

## Search

### 1. Search Groups

Search for study groups by category and/or keyword.

- **Endpoint**: `GET /groups/search`
- **Authentication**: Not required
- **Content-Type**: `application/json`

#### Query Parameters

| Parameter  | Type   | Required | Description           |
| ---------- | ------ | -------- | --------------------- |
| categoryId | number | No       | Filter by category ID |
| keyword    | string | No       | Search keyword        |

#### Example Request

```
GET /groups/search?categoryId=1&keyword=algorithm
```

#### Response: `200 OK`

```json
[
  {
    "id": 1,
    "groupName": "Algorithm Study",
    "description": "Weekly algorithm problem solving",
    "categoryId": 1,
    "subCategoryId": 2,
    "capacity": 10,
    "leaderId": 5,
    "isOnline": true,
    "location": "Zoom",
    "status": "RECRUITING",
    "createdAt": "2026-01-25T10:30:00Z"
  }
]
```

---

### 2. Search My Groups

Get all groups the authenticated user has joined or created.

- **Endpoint**: `GET /groups/search/me`
- **Authentication**: Required (JWT)
- **Content-Type**: `application/json`

#### Response: `200 OK`

```json
[
  {
    "id": 1,
    "groupName": "Algorithm Study",
    "description": "Weekly algorithm problem solving",
    "categoryId": 1,
    "subCategoryId": 2,
    "capacity": 10,
    "leaderId": 5,
    "isOnline": true,
    "location": "Zoom",
    "status": "RECRUITING",
    "createdAt": "2026-01-25T10:30:00Z"
  }
]
```

---

## Error Handling

### Error Response Format

All errors follow a consistent format:

```json
{
  "errorCode": 400,
  "message": "Error description",
  "timestamp": "2026-01-25T10:30:00"
}
```

### Common HTTP Status Codes

| Status Code | Description                                      |
| ----------- | ------------------------------------------------ |
| 200         | Success                                          |
| 400         | Bad Request - Invalid input                      |
| 401         | Unauthorized - Invalid or missing authentication |
| 403         | Forbidden - Insufficient permissions             |
| 404         | Not Found - Resource doesn't exist               |
| 409         | Conflict - Resource already exists               |
| 500         | Internal Server Error                            |

---

## Data Models

### Group Status Enum

| Value      | Description                   |
| ---------- | ----------------------------- |
| RECRUITING | 모집중 (Currently recruiting) |
| EXPIRED    | 만료됨 (Recruitment closed)   |

### User Role

| Value | Description   |
| ----- | ------------- |
| USER  | Regular user  |
| ADMIN | Administrator |

---

## Notes for Android Developers

1. **JWT Token Storage**: Store the access token securely (e.g., using EncryptedSharedPreferences) and include it in the Authorization header for authenticated requests.

2. **Token Refresh**: Currently, there's no refresh token mechanism visible in the API. Monitor for 401 responses and redirect to login when tokens expire.

3. **File Uploads**: For endpoints with `multipart/form-data`:
    - Use `MultipartBody.Part` in Retrofit
    - Set proper content type for images (e.g., `image/jpeg`, `image/png`)

4. **Date Format**: Timestamps are in ISO 8601 format (UTC timezone). Parse using `Instant` or similar date parsing libraries.

5. **OAuth Flow**:
    - Get Google ID token from Google Sign-In SDK
    - Send token to `/oauth/login/google`
    - If response type is `"REGISTER"`, proceed with email verification
    - If response type is `"LOGIN"`, use token as access token

6. **Email Verification Flow**:
    - After OAuth registration, get `register_token`
    - Send verification code request to `/auth/code`
    - User receives code via email (valid for 3 minutes)
    - Submit code with token to `/auth/verify`
    - Receive access token upon successful verification

7. **Group Operations**:
    - Group leader ID can be used to determine if current user is the leader
    - Only leaders can delete or expire groups
    - Check `capacity` field: `null` means unlimited capacity

8. **Search Optimization**:
    - Both `categoryId` and `keyword` are optional in search
    - You can use them together or separately
    - Empty query returns all groups

---

## Example Integration (Kotlin/Android)

### Retrofit Service Interface

```kotlin
interface StudyGroupApi {
    @POST("auth/login")
    suspend fun login(@Body request: LoginDto): LoginResponseDto

    @GET("groups/search")
    suspend fun searchGroups(
        @Query("categoryId") categoryId: Long?,
        @Query("keyword") keyword: String?
    ): List<GroupResponse>

    @GET("users/me")
    suspend fun getProfile(
        @Header("Authorization") token: String
    ): GetProfileDto

    @Multipart
    @PATCH("users/me")
    suspend fun updateProfile(
        @Header("Authorization") token: String,
        @Part("major") major: String?,
        @Part("nickname") nickname: String?,
        @Part profile_image: MultipartBody.Part?,
        @Part("bio") bio: String?
    ): GetProfileDto
}
```

---

**End of API Specification**
