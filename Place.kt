package com.example.couplemap

// 장소 정보를 담는 데이터 상자입니다.
data class Place(
    val id: String = "",           // 파이어베이스에서 구분할 고유 아이디
    val name: String = "",         // 가게 이름
    val address: String = "",      // 주소
    val category: String = "",     // 종류(맛집, 카페 등)
    val latitude: Double = 0.0,    // 위도
    val longitude: Double = 0.0    // 경도
)