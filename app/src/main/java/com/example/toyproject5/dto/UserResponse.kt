package com.example.toyproject5.dto

import com.google.gson.annotations.SerializedName

// 추후 nickname, email을 불러오는 dto도 이 파일에 정의될 예쩡입니다.
data class ImageResponse(
    @SerializedName("status") val status: String,
    @SerializedName("message") val message: String,
    @SerializedName("imageUrl") val imageUrl: String // todo:서버에 저장된 진짜 인터넷 주소
)