package com.example.studyrom

import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.PropertyName

data class StudyRoom(
    // Firestore 문서 ID는 따로 관리 (데이터베이스에 저장되지 않음)
    @get:Exclude var id: String = "",

    // @PropertyName을 사용하여 Firestore 필드명과 정확히 매칭
    @get:PropertyName("name") @set:PropertyName("name")
    var name: String = "",

    @get:PropertyName("capacity") @set:PropertyName("capacity")
    var capacity: Int = 4,

    // 🚨 핵심: 시간대별 예약 데이터를 담을 맵 필드
    @get:PropertyName("timeSlots") @set:PropertyName("timeSlots")
    var timeSlots: Map<String, String> = mutableMapOf()
) {
    // 파이어베이스에서 객체를 불러올 때 필요한 빈 생성자
    constructor() : this("", "", 4, mutableMapOf())
}