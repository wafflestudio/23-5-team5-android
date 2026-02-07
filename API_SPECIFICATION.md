# Study Group API Specification

## Base Information

- **Base URL**: `http://43.203.97.212:8080`
- **Version**: 1.0
- **Content-Type**: `application/json` (except for file upload endpoints)
- **Date**: 2026-02-07

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
9. [Review Management](#review-management)
10. [Error Handling](#error-handling)

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

- **Endpoint**: `GET /api/ping`
- **Authentication**: Not required
- **Response**: `200 OK`

```
pong
```

---

## User Management

### 1. Sign Up

Register a new user account.

- **Endpoint**: `POST /api/auth/signup`
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

- **Endpoint**: `POST /api/auth/login`
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

### 3. Logout

Invalidate the current access token.

- **Endpoint**: `POST /api/auth/logout`
- **Authentication**: Required (JWT)
- **Content-Type**: `application/json`

#### Response: `200 OK`

```
로그아웃 되었습니다.
```

---

### 4. Get Profile

Retrieve the authenticated user's profile information.

- **Endpoint**: `GET /api/users/me`
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

### 5. Update Profile

Update the authenticated user's profile information.

- **Endpoint**: `PATCH /api/users/me`
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

### 6. Update Profile Image

Update only the profile image.

- **Endpoint**: `PUT /api/users/me/profile-image`
- **Authentication**: Required (JWT)
- **Content-Type**: `multipart/form-data`

#### Request Body (Form Data)

| Field         | Type | Required | Description            |
| ------------- | ---- | -------- | ---------------------- |
| profile_image | file | Yes      | New profile image file |

#### Response: `200 OK`

```json
{
  "profileImageUrl": "https://...",
  "createdAt": "2026-01-25T10:30:00Z"
}
```

---

### 7. Get Profile Image

Get a redirect to the profile image URL.

- **Endpoint**: `GET /api/users/me/profile-image`
- **Authentication**: Required (JWT)

#### Response: `302 Found`

Redirects to the profile image URL, or returns `404 Not Found` if no profile image exists.

---

### 8. Get Other User's Profile

Retrieve another user's profile information by user ID.

- **Endpoint**: `POST /api/users/search/profile`
- **Authentication**: Required (JWT)
- **Content-Type**: `application/json`

#### Request Body

```json
{
  "user_id": 123
}
```

| Field   | Type   | Required | Description                        |
| ------- | ------ | -------- | ---------------------------------- |
| user_id | number | Yes      | ID of the user to retrieve profile |

#### Response: `200 OK`

```json
{
  "userId": 123,
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

- **Endpoint**: `POST /api/oauth/login/{provider}`
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

### Social Sign Up (Additional Information)

Complete social sign-up by providing additional user information.

- **Endpoint**: `POST /api/oauth/signUp/{provider}`
- **Authentication**: Not required (but requires registerToken)
- **Content-Type**: `application/json`

#### Path Parameters

| Parameter | Type   | Description                                            |
| --------- | ------ | ------------------------------------------------------ |
| provider  | string | OAuth provider (currently supports: `google`, `kakao`) |

#### Request Body

```json
{
  "registerToken": "string",
  "email": "myid@snu.ac.kr",
  "major": "string",
  "student_number": "string",
  "nickname": "string"
}
```

| Field          | Type   | Required | Description                                               |
| -------------- | ------ | -------- | --------------------------------------------------------- |
| registerToken  | string | Yes      | Registration token from OAuth login response              |
| email          | string | No       | SNU email (null if social account already uses SNU email) |
| major          | string | Yes      | User's major/department                                   |
| student_number | string | Yes      | Student identification number                             |
| nickname       | string | Yes      | User's nickname                                           |

#### Response: `200 OK`

```json
{
  "accessToken": "string",
  "username": "string",
  "nickname": "string",
  "isVerified": true
}
```

---

## Email Verification

### 1. Send Verification Code

Send a verification code to the user's email.

- **Endpoint**: `POST /api/auth/code`
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

### 2. Verify Email Code (Regular Signup)

Verify the email with the code sent for regular (non-social) signup.

- **Endpoint**: `POST /api/auth/verify`
- **Authentication**: Not required
- **Content-Type**: `application/json`

#### Request Body

```json
{
  "email": "user@snu.ac.kr",
  "code": "123456"
}
```

| Field | Type   | Required | Description                  |
| ----- | ------ | -------- | ---------------------------- |
| email | string | Yes      | Email address being verified |
| code  | string | Yes      | Verification code from email |

#### Response: `200 OK`

```json
{
  "message": "인증에 성공하였습니다."
}
```

---

### 3. Social Verify (OAuth Email Verification)

Verify email for social login users who don't have SNU email.

- **Endpoint**: `POST /api/auth/social/verify`
- **Authentication**: Not required (but requires register_token)
- **Content-Type**: `application/json`

#### Request Body

```json
{
  "register_token": "string",
  "email": "user@snu.ac.kr",
  "code": "123456"
}
```

| Field          | Type   | Required | Description                                  |
| -------------- | ------ | -------- | -------------------------------------------- |
| register_token | string | Yes      | Registration token from OAuth login response |
| email          | string | Yes      | SNU email address being verified             |
| code           | string | Yes      | Verification code from email                 |

#### Response: `200 OK`

```json
{
  "type": "REGISTER",
  "token": "string"
}
```

| Field | Description                                                     |
| ----- | --------------------------------------------------------------- |
| type  | Either `"LOGIN"` (existing user) or `"REGISTER"` (new user)     |
| token | AccessToken (if type=LOGIN) or RegisterToken (if type=REGISTER) |

**Note**: If `type` is `"REGISTER"`, proceed to `/oauth/signUp/{provider}` with additional information.

---

## Group Management

### 1. Create Group

Create a new study group.

- **Endpoint**: `POST /api/groups`
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

#### Response: `201 Created`

```json
{
  "id": 1,
  "groupName": "Algorithm Study",
  "description": "Weekly algorithm problem solving",
  "categoryId": 1,
  "subCategoryId": 2,
  "capacity": 10,
  "leaderId": 5,
  "leaderNickname": "John",
  "leaderBio": "Backend developer",
  "leaderUserName": "john@snu.ac.kr",
  "leaderProfileImageUrl": "https://s3.ap-northeast-2.amazonaws.com/team5-bucket/profiles/user_5.jpg",
  "isOnline": true,
  "location": "Zoom",
  "status": "RECRUITING",
  "createdAt": "2026-01-25T10:30:00Z"
}
```

---

### 2. Delete Group

Delete a study group. Only the group leader can delete.

- **Endpoint**: `DELETE /api/groups`
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

```json
{
  "deletedId": 1
}
```

---

### 3. Expire Group

Mark a group as expired (close recruitment). Only the group leader can expire.

- **Endpoint**: `PATCH /api/groups/expire`
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

```json
{
  "expiredId": 1
}
```

---

## User-Group Operations

### 1. Join Group

Join an existing study group.

- **Endpoint**: `POST /api/groups/join`
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

- **Endpoint**: `DELETE /api/groups/join`
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

Search for study groups by category, subcategory, and/or keyword using cursor-based pagination.

- **Endpoint**: `GET /api/groups/search`
- **Authentication**: Not required
- **Content-Type**: `application/json`

#### Query Parameters

| Parameter     | Type   | Required | Description                                             |
| ------------- | ------ | -------- | ------------------------------------------------------- |
| categoryId    | number | No       | Filter by main category ID                              |
| subCategoryId | number | No       | Filter by sub-category ID                               |
| keyword       | string | No       | Search keyword (searches in group name and description) |
| cursorId      | number | No       | Last item ID from previous page (null for first page)   |
| size          | number | No       | Number of items per page (1-50, default: 10)            |

#### Example Request

```
GET /groups/search?categoryId=1&keyword=algorithm&cursorId=null&size=10
```

#### Response: `200 OK`

```json
{
  "content": [
    {
      "id": 125,
      "groupName": "Algorithm Study",
      "description": "Weekly algorithm problem solving",
      "categoryId": 1,
      "subCategoryId": 2,
      "capacity": 10,
      "leaderId": 5,
      "leaderNickname": "John",
      "leaderBio": "Backend developer",
      "leaderUserName": "john@snu.ac.kr",
      "leaderProfileImageUrl": "https://s3.ap-northeast-2.amazonaws.com/team5-bucket/profiles/user_5.jpg",
      "isOnline": true,
      "location": "Zoom",
      "status": "RECRUITING",
      "createdAt": "2026-01-25T10:30:00Z"
    },
    {
      "id": 122,
      "groupName": "Data Structures Study",
      "description": "Deep dive into data structures",
      "categoryId": 1,
      "subCategoryId": 2,
      "capacity": 8,
      "leaderId": 12,
      "leaderNickname": "Jane",
      "leaderBio": "CS student",
      "leaderUserName": "jane@snu.ac.kr",
      "leaderProfileImageUrl": "https://s3.ap-northeast-2.amazonaws.com/team5-bucket/profiles/user_12.jpg",
      "isOnline": false,
      "location": "Seoul Campus",
      "status": "RECRUITING",
      "createdAt": "2026-01-24T15:20:00Z"
    }
  ],
  "nextCursorId": 122,
  "hasNext": true
}
```

---

### 2. Search My Created Groups

Get all groups created by the authenticated user (groups where user is the leader) using cursor-based pagination.

- **Endpoint**: `GET /api/groups/search/me`
- **Authentication**: Required (JWT)
- **Content-Type**: `application/json`

#### Query Parameters

| Parameter | Type   | Required | Description                                           |
| --------- | ------ | -------- | ----------------------------------------------------- |
| cursorId  | number | No       | Last item ID from previous page (null for first page) |
| size      | number | No       | Number of items per page (1-50, default: 10)          |

#### Response: `200 OK`

```json
{
  "content": [
    {
      "id": 150,
      "groupName": "Kotlin Mastery",
      "description": "Deep dive into Kotlin coroutines",
      "categoryId": 1,
      "subCategoryId": 11,
      "capacity": 5,
      "leaderId": 501,
      "leaderNickname": "MyNickname",
      "leaderBio": "Backend developer",
      "leaderUserName": "me@snu.ac.kr",
      "leaderProfileImageUrl": "https://s3.ap-northeast-2.amazonaws.com/team5-bucket/profiles/user_501.jpg",
      "isOnline": true,
      "location": "Google Meet",
      "status": "RECRUITING",
      "createdAt": "2026-02-01T14:00:00Z"
    },
    {
      "id": 142,
      "groupName": "Daily Algorithm Practice",
      "description": "Daily leetcode problems",
      "categoryId": 1,
      "subCategoryId": 15,
      "capacity": 8,
      "leaderId": 501,
      "leaderNickname": "MyNickname",
      "leaderBio": "Backend developer",
      "leaderUserName": "me@snu.ac.kr",
      "leaderProfileImageUrl": "https://s3.ap-northeast-2.amazonaws.com/team5-bucket/profiles/user_501.jpg",
      "isOnline": true,
      "location": "Slack/Github",
      "status": "RECRUITING",
      "createdAt": "2026-01-20T09:00:00Z"
    }
  ],
  "nextCursorId": 142,
  "hasNext": false
}
```

---

### 3. Search My Joined Groups

Get all groups the authenticated user has joined as a member (not as a leader) using cursor-based pagination.

- **Endpoint**: `GET /api/groups/search/joined`
- **Authentication**: Required (JWT)
- **Content-Type**: `application/json`

#### Query Parameters

| Parameter | Type   | Required | Description                                           |
| --------- | ------ | -------- | ----------------------------------------------------- |
| cursorId  | number | No       | Last item ID from previous page (null for first page) |
| size      | number | No       | Number of items per page (1-50, default: 10)          |

#### Response: `200 OK`

```json
{
  "content": [
    {
      "id": 125,
      "groupName": "Backend Study",
      "description": "Clean architecture and TDD",
      "categoryId": 1,
      "subCategoryId": 11,
      "capacity": 6,
      "leaderId": 501,
      "leaderNickname": "TeamLeader",
      "leaderBio": "Senior developer",
      "leaderUserName": "leader@snu.ac.kr",
      "leaderProfileImageUrl": "https://s3.ap-northeast-2.amazonaws.com/team5-bucket/profiles/user_501.jpg",
      "isOnline": false,
      "location": "Gangnam Station Cafe",
      "status": "RECRUITING",
      "createdAt": "2026-02-01T10:00:00Z"
    },
    {
      "id": 119,
      "groupName": "Real Estate Investment Study",
      "description": "Real estate auction analysis",
      "categoryId": 3,
      "subCategoryId": 35,
      "capacity": 10,
      "leaderId": 44,
      "leaderNickname": "PropertyExpert",
      "leaderBio": "Investment professional",
      "leaderUserName": "expert@snu.ac.kr",
      "leaderProfileImageUrl": "https://s3.ap-northeast-2.amazonaws.com/team5-bucket/profiles/user_44.jpg",
      "isOnline": false,
      "location": "Bundang, Seongnam",
      "status": "EXPIRED",
      "createdAt": "2026-01-25T09:00:00Z"
    }
  ],
  "nextCursorId": 119,
  "hasNext": true
}
```

---

### 4. Search Users In Group

Get all users participating in a specific study group using cursor-based pagination.

- **Endpoint**: `GET /api/users/search`
- **Authentication**: Required (JWT)
- **Content-Type**: `application/json`

#### Query Parameters

| Parameter | Type   | Required | Description                                           |
| --------- | ------ | -------- | ----------------------------------------------------- |
| groupId   | number | Yes      | ID of the group to search users in                    |
| cursorId  | number | No       | Last item ID from previous page (null for first page) |
| size      | number | No       | Number of items per page (1-50, default: 10)          |

#### Response: `200 OK`

```json
{
  "content": [
    {
      "userId": 101,
      "username": "user1@snu.ac.kr",
      "nickname": "UserOne",
      "major": "Computer Science",
      "studentNumber": "2020-12345",
      "profileImageUrl": "https://...",
      "bio": "Backend developer",
      "role": "USER",
      "createdAt": "2026-01-15T10:30:00Z"
    },
    {
      "userId": 102,
      "username": "user2@snu.ac.kr",
      "nickname": "UserTwo",
      "major": "Electrical Engineering",
      "studentNumber": "2021-23456",
      "profileImageUrl": "https://...",
      "bio": "ML enthusiast",
      "role": "USER",
      "createdAt": "2026-01-18T14:20:00Z"
    }
  ],
  "nextCursorId": 102,
  "hasNext": false
}
```

---

## Review Management

### 1. Create Review

Create a review for a group member.

- **Endpoint**: `POST /api/reviews`
- **Authentication**: Required (JWT)
- **Content-Type**: `application/json`

#### Request Body

```json
{
  "groupId": 1,
  "revieweeId": 5,
  "rating": 4,
  "comment": "Great teamwork and communication skills!"
}
```

| Field      | Type   | Required | Description                   |
| ---------- | ------ | -------- | ----------------------------- |
| groupId    | number | Yes      | ID of the group               |
| revieweeId | number | Yes      | ID of the user being reviewed |
| rating     | number | Yes      | Rating score (typically 1-5)  |
| comment    | string | No       | Review comment/feedback       |

#### Response: `200 OK`

```json
{
  "id": 1,
  "groupId": 1,
  "reviewerId": 10,
  "revieweeId": 5,
  "rating": 4,
  "comment": "Great teamwork and communication skills!",
  "createdAt": "2026-02-01T10:30:00Z",
  "updatedAt": "2026-02-01T10:30:00Z"
}
```

---

### 2. Update Review

Update an existing review.

- **Endpoint**: `PATCH /api/reviews`
- **Authentication**: Required (JWT)
- **Content-Type**: `application/json`

#### Request Body

```json
{
  "reviewId": 1,
  "rating": 5,
  "comment": "Updated: Excellent collaboration!"
}
```

| Field    | Type   | Required | Description                |
| -------- | ------ | -------- | -------------------------- |
| reviewId | number | Yes      | ID of the review to update |
| rating   | number | No       | Updated rating score       |
| comment  | string | No       | Updated review comment     |

#### Response: `200 OK`

```json
{
  "id": 1,
  "groupId": 1,
  "reviewerId": 10,
  "revieweeId": 5,
  "rating": 5,
  "comment": "Updated: Excellent collaboration!",
  "createdAt": "2026-02-01T10:30:00Z",
  "updatedAt": "2026-02-01T15:45:00Z"
}
```

---

### 3. Delete Review

Delete a review. Only the reviewer can delete their own review.

- **Endpoint**: `DELETE /api/reviews`
- **Authentication**: Required (JWT)
- **Content-Type**: `application/json`

#### Request Body

```json
{
  "reviewId": 1
}
```

| Field    | Type   | Required | Description                |
| -------- | ------ | -------- | -------------------------- |
| reviewId | number | Yes      | ID of the review to delete |

#### Response: `200 OK`

```
(Empty response body)
```

---

### 4. Search Reviews

Search for reviews with optional filters using page-based pagination.

- **Endpoint**: `GET /api/reviews/search`
- **Authentication**: Not required
- **Content-Type**: `application/json`

#### Query Parameters

| Parameter  | Type   | Required | Description                           |
| ---------- | ------ | -------- | ------------------------------------- |
| groupId    | number | No       | Filter by group ID                    |
| reviewerId | number | No       | Filter by reviewer (author) user ID   |
| revieweeId | number | No       | Filter by reviewee (reviewed user) ID |
| page       | number | No       | Page number (0-indexed, default: 0)   |
| size       | number | No       | Page size (default: 10)               |
| sort       | string | No       | Sort field (default: createdAt)       |

#### Example Request

```
GET /reviews/search?groupId=1&page=0&size=10
```

#### Response: `200 OK`

```json
{
  "content": [
    {
      "id": 1,
      "groupId": 1,
      "reviewerId": 10,
      "reviewerNickname": "John",
      "revieweeId": 5,
      "revieweeNickname": "Jane",
      "rating": 4,
      "comment": "Great teamwork!",
      "createdAt": "2026-02-01T10:30:00Z",
      "updatedAt": "2026-02-01T10:30:00Z"
    },
    {
      "id": 2,
      "groupId": 1,
      "reviewerId": 12,
      "reviewerNickname": "Mike",
      "revieweeId": 5,
      "revieweeNickname": "Jane",
      "rating": 5,
      "comment": "Excellent communication!",
      "createdAt": "2026-02-01T12:15:00Z",
      "updatedAt": "2026-02-01T12:15:00Z"
    }
  ],
  "pageable": {
    "sort": {
      "sorted": true,
      "unsorted": false,
      "empty": false
    },
    "pageNumber": 0,
    "pageSize": 10,
    "offset": 0,
    "paged": true,
    "unpaged": false
  },
  "totalPages": 1,
  "totalElements": 2,
  "last": true,
  "first": true,
  "size": 10,
  "number": 0,
  "sort": {
    "sorted": true,
    "unsorted": false,
    "empty": false
  },
  "numberOfElements": 2,
  "empty": false
}
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
| 201         | Created - Resource successfully created          |
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
    - Get Google/Kakao ID token from respective SDK
    - Send token to `/api/oauth/login/{provider}` (google or kakao)
    - If response type is `"LOGIN"`, use token as access token (login complete)
    - If response type is `"REGISTER"`, check the email in the token:
        - If SNU email: Set `email` field to `null` in signup request
        - If non-SNU email: Proceed with email verification flow

6. **Email Verification Flow**:
    - **For Regular Signup**: Send email to `/api/auth/code`, verify with `/api/auth/verify` (returns success message)
    - **For OAuth with non-SNU email**:
        - Get `register_token` from OAuth login response
        - Send verification code request to `/api/auth/code`
        - User receives code via email (valid for 3 minutes)
        - Submit code with token to `/api/auth/social/verify`
        - If response type is `"REGISTER"`, proceed to signup with additional info

7. **OAuth Signup Completion**:
    - After verification (or if email is SNU email), call `/api/oauth/signUp/{provider}`
    - Provide `registerToken`, `email` (null if SNU), `major`, `student_number`, `nickname`
    - Receive access token and complete registration

8. **Logout**:
    - Call `POST /api/auth/logout` with the Bearer token in the Authorization header
    - The token will be invalidated and added to a blacklist
    - User must log in again to get a new token

9. **Cursor-Based Pagination** (for group and user search):
    - Most search endpoints use cursor-based pagination for infinite scrolling
    - Response format: `{ content: [...], nextCursorId: number, hasNext: boolean }`
    - For first page: send `cursorId=null` or omit the parameter
    - For subsequent pages: use `nextCursorId` from previous response as `cursorId` parameter
    - Check `hasNext` to determine if there are more pages
    - Size parameter controls items per page (1-50, default: 10)

10. **Page-Based Pagination** (for reviews):
    - Review search uses traditional page-based pagination
    - Response uses Spring Data's `Page` object
    - Use `page` (0-indexed), `size`, and `sort` parameters
    - Access results via the `content` array in the response
    - Check `totalPages`, `totalElements`, `first`, `last` for pagination state

11. **Group Operations**:
    - Group responses now include leader information (nickname, bio, username, profile image URL)
    - Group leader ID can be used to determine if current user is the leader
    - Only leaders can delete or expire groups
    - Check `capacity` field: `null` means unlimited capacity
    - Create group returns `201 Created` with the created group object
    - Delete and expire operations return the affected ID in response body

12. **Search Optimization**:
    - Group search: `categoryId`, `subCategoryId`, and `keyword` are all optional
    - You can use them together or separately
    - Empty query returns all groups (paginated)
    - Three separate endpoints for different group types:
        - `/api/groups/search` - All groups (public search)
        - `/api/groups/search/me` - Groups I created (as leader)
        - `/api/groups/search/joined` - Groups I joined (as member)

13. **Review Management**:
    - Reviews can be created, updated, deleted, and searched
    - Only the original reviewer can update or delete their review
    - Search supports filtering by groupId, reviewerId, or revieweeId
    - Reviews use page-based pagination (not cursor-based)

14. **User Profile Access**:
    - You can view your own profile with `GET /api/users/me`
    - You can view other users' profiles with `POST /api/users/search/profile` by providing their user ID
    - Both endpoints return the same profile structure

---

## Example Integration (Kotlin/Android)

### Retrofit Service Interface

```kotlin
interface StudyGroupApi {
    // Authentication
    @POST("api/auth/login")
    suspend fun login(@Body request: LoginDto): LoginResponseDto

    @POST("api/auth/logout")
    suspend fun logout(@Header("Authorization") token: String): String

    // Group Search (Cursor-based pagination)
    @GET("api/groups/search")
    suspend fun searchGroups(
        @Query("categoryId") categoryId: Long?,
        @Query("subCategoryId") subCategoryId: Long?,
        @Query("keyword") keyword: String?,
        @Query("cursorId") cursorId: Long?,
        @Query("size") size: Int = 10
    ): CursorResponse<GroupSearchResponse>

    @GET("api/groups/search/me")
    suspend fun searchMyGroups(
        @Header("Authorization") token: String,
        @Query("cursorId") cursorId: Long?,
        @Query("size") size: Int = 10
    ): CursorResponse<GroupSearchResponse>

    @GET("api/groups/search/joined")
    suspend fun searchJoinedGroups(
        @Header("Authorization") token: String,
        @Query("cursorId") cursorId: Long?,
        @Query("size") size: Int = 10
    ): CursorResponse<GroupSearchResponse>

    // User Search (Cursor-based pagination)
    @GET("api/users/search")
    suspend fun searchUsersInGroup(
        @Header("Authorization") token: String,
        @Query("groupId") groupId: Long,
        @Query("cursorId") cursorId: Long?,
        @Query("size") size: Int = 10
    ): CursorResponse<UserSearchResponseDto>

    // User Profile
    @GET("api/users/me")
    suspend fun getProfile(
        @Header("Authorization") token: String
    ): GetProfileDto

    @Multipart
    @PATCH("api/users/me")
    suspend fun updateProfile(
        @Header("Authorization") token: String,
        @Part("major") major: String?,
        @Part("nickname") nickname: String?,
        @Part profile_image: MultipartBody.Part?,
        @Part("bio") bio: String?
    ): GetProfileDto

    // Group Management
    @POST("api/groups")
    suspend fun createGroup(
        @Header("Authorization") token: String,
        @Body request: CreateGroupDto
    ): GroupSearchResponse

    @DELETE("api/groups")
    suspend fun deleteGroup(
        @Header("Authorization") token: String,
        @Body request: DeleteGroupDto
    ): Map<String, Long>

    // Reviews (Page-based pagination)
    @GET("api/reviews/search")
    suspend fun searchReviews(
        @Query("groupId") groupId: Long?,
        @Query("reviewerId") reviewerId: Long?,
        @Query("revieweeId") revieweeId: Long?,
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 10,
        @Query("sort") sort: String = "createdAt"
    ): Page<ReviewResponse>

    @POST("api/reviews")
    suspend fun createReview(
        @Header("Authorization") token: String,
        @Body request: CreateReviewDto
    ): Review
}

// Data classes for cursor-based pagination
data class CursorResponse<T>(
    val content: List<T>,
    val nextCursorId: Long?,
    val hasNext: Boolean
)
```

---

**End of API Specification**
