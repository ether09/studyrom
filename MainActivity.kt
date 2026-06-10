package com.example.studyrom

import android.graphics.Color
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.firestore.FirebaseFirestore
import androidx.appcompat.app.AlertDialog // 이 항목이 가장 중요합니다!

class MainActivity : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()
    private lateinit var adapter: RoomAdapter
    private val roomList = mutableListOf<StudyRoom>()
    private lateinit var myStudentId: String
    private lateinit var tvWelcomeMessage: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        myStudentId = intent.getStringExtra("STUDENT_ID") ?: "20261111"
        tvWelcomeMessage = findViewById(R.id.tvWelcomeMessage)
        tvWelcomeMessage.text = "${myStudentId}님, 환영합니다! ✍️"

        val recyclerView = findViewById<RecyclerView>(R.id.rvStudyRooms)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val btnSnack = findViewById<ImageButton>(R.id.btnSnack)
        btnSnack.setOnClickListener {
            showSnackDialog()
        }

        adapter = RoomAdapter(
            roomList,
            myStudentId,
            onItemClick = { selectedRoom ->
                // 바텀 시트를 호출하도록 수정
                showTimeBottomSheet(selectedRoom)
            },
            onImageClick = { imageRes -> showImageDetail(imageRes) }
        )
        recyclerView.adapter = adapter

        listenToStudyRooms()
    }

    // 🚨 바텀 시트 호출 함수
    private fun showTimeBottomSheet(room: StudyRoom) {
        val bottomSheet = BottomSheetDialog(this)
        // 1. dialog_time_selection.xml 레이아웃 사용
        val view = layoutInflater.inflate(R.layout.dialog_time_selection, null)
        val gridLayout = view.findViewById<GridLayout>(R.id.gridLayout)

        val allTimes = listOf("09", "10", "11", "12", "13", "14", "15", "16", "17", "18")
        bottomSheet.setContentView(view)

        for (time in allTimes) {
            // 2. 새로운 디자인의 아이템(item_time_slot.xml)을 가져와서 생성
            val itemView = layoutInflater.inflate(R.layout.item_time_slot, null)
            val tvTime = itemView.findViewById<TextView>(R.id.tvTime)
            val tvStatus = itemView.findViewById<TextView>(R.id.tvStatus)

            tvTime.text = "$time:00"

            // 3. 상태 체크 로직
            val isBooked = !room.timeSlots[time].isNullOrEmpty()
            val isMySlot = room.timeSlots[time] == myStudentId

            when {
                isMySlot -> { // 내 예약
                    itemView.setBackgroundColor(Color.parseColor("#FFF3E0")) // 연한 주황
                    tvStatus.text = "내 예약"
                    tvStatus.setTextColor(Color.parseColor("#E65100"))
                    itemView.setOnClickListener {
                        updateRoomBooking(room.id, time, "")
                        bottomSheet.dismiss()
                    }
                }
                isBooked -> { // 마감
                    itemView.setBackgroundColor(Color.parseColor("#F5F5F5"))
                    tvStatus.text = "마감"
                    tvStatus.setTextColor(Color.GRAY)
                }
                else -> { // 예약 가능
                    itemView.setBackgroundColor(Color.parseColor("#E8F5E9")) // 연한 초록
                    tvStatus.text = "예약 가능"
                    tvStatus.setTextColor(Color.parseColor("#2E7D32"))
                    itemView.setOnClickListener {
                        updateRoomBooking(room.id, time, myStudentId)
                        bottomSheet.dismiss()
                    }
                }
            }

            // 4. Grid에 추가
            val params = GridLayout.LayoutParams()
            params.width = 0
            params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f)
            params.setMargins(8, 8, 8, 8)
            itemView.layoutParams = params

            gridLayout.addView(itemView)
        }
        bottomSheet.show()
    }

    // 예약/취소 통합 업데이트 함수
    private fun updateRoomBooking(roomId: String, timeSlot: String, studentId: String) {
        val roomRef = db.collection("study_rooms").document(roomId)
        db.runTransaction { transaction ->
            val snapshot = transaction.get(roomRef)
            val timeSlots = snapshot.get("timeSlots") as? MutableMap<String, String> ?: mutableMapOf()
            timeSlots[timeSlot] = studentId
            transaction.update(roomRef, "timeSlots", timeSlots)
        }.addOnSuccessListener {
            val msg = if (studentId.isEmpty()) "취소 완료!" else "예약 완료!"
            Toast.makeText(this, "$timeSlot 시 $msg", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showImageDetail(imageRes: Int) {
        val builder = AlertDialog.Builder(this)
        val iv = ImageView(this)

        // 1. 이미지를 가로 너비에 꽉 차게 설정
        iv.layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )

        iv.setImageResource(imageRes)

        // 2. 중요: 이미지 비율을 유지하면서 화면 안에 다 보이게 설정
        iv.adjustViewBounds = true
        iv.scaleType = ImageView.ScaleType.FIT_CENTER

        // 3. 여백 추가
        iv.setPadding(32, 32, 32, 32)

        builder.setView(iv)
        val dialog = builder.create()

        // 배경을 투명하게 설정하면 더 세련되게 보입니다
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        dialog.show()
        iv.setOnClickListener { dialog.dismiss() }
    }

    private fun listenToStudyRooms() {
        db.collection("study_rooms").addSnapshotListener { snapshot, _ ->
            if (snapshot != null) {
                roomList.clear()
                for (doc in snapshot) {
                    val room = doc.toObject(StudyRoom::class.java)
                    room.id = doc.id
                    roomList.add(room)
                }
                adapter.notifyDataSetChanged()
            }
        }
    }

    private fun showSnackDialog() {
        val bottomSheet = BottomSheetDialog(this)
        val view = layoutInflater.inflate(R.layout.dialog_time_selection, null)
        val gridLayout = view.findViewById<GridLayout>(R.id.gridLayout)

        val snacks = listOf(
            "편의점" to "1층, 24시간 운영",
            "XX카페" to "도보 2분, 커피맛집",
            "YY분식" to "도보 5분, 떡볶이",
            "ZZ샌드위치" to "도보 3분, 아침식사"
        )

        bottomSheet.setContentView(view)

        for (snack in snacks) {
            val itemView = layoutInflater.inflate(R.layout.item_time_slot, null)
            val tvTime = itemView.findViewById<TextView>(R.id.tvTime)
            val tvStatus = itemView.findViewById<TextView>(R.id.tvStatus)

            tvTime.text = snack.first
            tvStatus.text = snack.second
            tvStatus.setTextColor(Color.DKGRAY)
            itemView.setBackgroundColor(Color.parseColor("#FFFDE7"))

            // 🚨 여기에 지도로 연결하는 코드를 넣었습니다!
            itemView.setOnClickListener {
                // "geo:0,0?q=편의점" 같은 형식으로 지도 앱 호출
                val gmmIntentUri = android.net.Uri.parse("geo:0,0?q=${snack.first}")
                val mapIntent = android.content.Intent(android.content.Intent.ACTION_VIEW, gmmIntentUri)

                // 지도 앱이 설치되어 있는지 확인 후 실행
                if (mapIntent.resolveActivity(packageManager) != null) {
                    startActivity(mapIntent)
                } else {
                    Toast.makeText(this, "지도 앱을 찾을 수 없습니다.", Toast.LENGTH_SHORT).show()
                }
                bottomSheet.dismiss()
            }

            val params = GridLayout.LayoutParams()
            params.width = 0
            params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f)
            params.setMargins(8, 8, 8, 8)
            itemView.layoutParams = params

            gridLayout.addView(itemView)
        }
        bottomSheet.show()
    }

}