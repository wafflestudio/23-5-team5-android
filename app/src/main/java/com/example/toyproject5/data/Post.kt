package com.example.toyproject5.data

data class Post(
    val id: String,
    val userId: String,
    val title: String,
    val description: String,
    val category: String,
    val field: String? = null,
    val date: String? = null,
    val location: String? = null,
    val authorName: String,
    val authorEmail: String,
    val createdAt: String,
    val isClosed: Boolean = false,
    val participants: List<Participant> = emptyList()
)

data class Participant(
    val userId: String,
    val nickname: String,
    val email: String
)

val mockPosts = listOf(
    Post(
        id = "1",
        userId = "user1",
        title = "토익 스터디원 모집합니다",
        description = "토익 900점을 목표로 함께 공부할 스터디원을 모집합니다. 현재 3명이 활동 중이며, 2명 더 모집합니다.",
        category = "스터디",
        field = "영어",
        date = "매주 화, 목 오후 7시",
        location = "중앙도서관 3층",
        authorName = "이영희",
        authorEmail = "younghee@university.ac.kr",
        createdAt = "2026-01-05"
    ),
    Post(
        id = "2",
        userId = "user2",
        title = "코딩테스트 스터디",
        description = "알고리즘 문제 풀이 및 코딩테스트 대비 스터디입니다. 백준, 프로그래머스 위주로 진행합니다.",
        category = "취준",
        field = "IT/개발",
        date = "매일 오전 10시",
        location = "온라인 (Discord)",
        authorName = "김철수",
        authorEmail = "chulsoo@university.ac.kr",
        createdAt = "2026-01-04"
    ),
    Post(
        id = "3",
        userId = "user3",
        title = "창업 동아리 팀원 모집",
        description = "AI 기반 서비스 창업을 준비하고 있습니다. 개발자, 디자이너, 마케터 모두 환영합니다.",
        category = "대외활동",
        field = "창업",
        date = "주 2회 미팅",
        location = "창업보육센터",
        authorName = "윤서준",
        authorEmail = "seojun@university.ac.kr",
        createdAt = "2026-01-03"
    ),
    Post(
        id = "4",
        userId = "user4",
        title = "공무원 시험 스터디",
        description = "9급 공무원 시험 준비 스터디입니다. 현재 5명이 활동 중입니다.",
        category = "고시",
        field = "행정직",
        date = "매일 오후 2시-6시",
        location = "스터디카페 (학교 앞)",
        authorName = "박민지",
        authorEmail = "minji@university.ac.kr",
        createdAt = "2026-01-02"
    ),
    Post(
        id = "5",
        userId = "user5",
        title = "자격증 준비 스터디 (정보처리기사)",
        description = "정보처리기사 자격증 준비를 위한 스터디입니다. 필기 시험 대비 중입니다.",
        category = "스터디",
        field = "IT",
        date = "주 3회",
        location = "온라인",
        authorName = "최지훈",
        authorEmail = "jihoon@university.ac.kr",
        createdAt = "2026-01-01"
    )
)
