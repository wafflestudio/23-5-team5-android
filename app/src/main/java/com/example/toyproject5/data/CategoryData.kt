package com.example.toyproject5.data

data class Category(val id: Int, val name: String, val subcategories: List<Subcategory>)
data class Subcategory(val id: Int, val name: String)

val categoryList = listOf(
    Category(1, "스터디", listOf(
        Subcategory(1, "어학"),
        Subcategory(2, "전공"),
        Subcategory(3, "자격증"),
        Subcategory(4, "기타")
    )),
    Category(2, "고시", listOf(
        Subcategory(5, "행정"),
        Subcategory(6, "기술"),
        Subcategory(7, "임용"),
        Subcategory(8, "기타")
    )),
    Category(3, "취준", listOf(
        Subcategory(9, "코딩테스트"),
        Subcategory(10, "면접"),
        Subcategory(11, "인턴"),
        Subcategory(12, "기타")
    )),
    Category(4, "대외활동", listOf(
        Subcategory(13, "공모전"),
        Subcategory(14, "서포터즈"),
        Subcategory(15, "봉사"),
        Subcategory(16, "기타")
    ))
)

fun getCategoryName(id: Int): String = categoryList.find { it.id == id }?.name ?: "기타"

fun getSubcategoryName(categoryId: Int, subId: Int): String? {
    if (subId <= 0) return null
    return categoryList.find { it.id == categoryId }?.subcategories?.find { it.id == subId }?.name
}
