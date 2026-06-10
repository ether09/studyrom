package com.example.studyrom

import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class RoomAdapter(
    private val roomList: List<StudyRoom>,
    private val myStudentId: String,
    private val onItemClick: (StudyRoom) -> Unit,
    private val onImageClick: (Int) -> Unit
) : RecyclerView.Adapter<RoomAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvRoomName: TextView = view.findViewById(R.id.tvRoomName)
        val tvCapacity: TextView = view.findViewById(R.id.tvCapacity)
        val tvStatusText: TextView = view.findViewById(R.id.tvStatusText)
        val btnStatus: Button = view.findViewById(R.id.btnStatus)
        val ivRoomImage: ImageView = view.findViewById(R.id.ivRoomImage)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_room, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val room = roomList[position]
        holder.tvRoomName.text = room.name
        holder.tvCapacity.text = "최대 ${room.capacity}명" // 수정: capacity 필드 사용

        // 이미지 로직
        val imageRes = when (room.capacity) {
            4 -> R.drawable.room4
            5 -> R.drawable.room5
            6 -> R.drawable.room6
            8 -> R.drawable.room8
            else -> R.drawable.ic_launcher_foreground
        }
        holder.ivRoomImage.setImageResource(imageRes)
        holder.ivRoomImage.setOnClickListener { onImageClick(imageRes) }

        // 상태 로직
        val isMyBooking = room.timeSlots.containsValue(myStudentId)
        val hasAvailableSlot = room.timeSlots.containsValue("")

        // UI 상태 업데이트
        when {
            isMyBooking -> {
                holder.tvStatusText.text = "● 내 예약 있음"
                holder.tvStatusText.setTextColor(Color.parseColor("#E65100"))
                setRoundBackground(holder.tvStatusText, "#FFF3E0")

                holder.btnStatus.text = "관리"
                holder.btnStatus.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#FF9800"))
            }
            hasAvailableSlot -> {
                holder.tvStatusText.text = "● 예약 가능"
                holder.tvStatusText.setTextColor(Color.parseColor("#2E7D32"))
                setRoundBackground(holder.tvStatusText, "#E8F5E9")

                holder.btnStatus.text = "예약"
                holder.btnStatus.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#4CAF50"))
            }
            else -> {
                holder.tvStatusText.text = "예약 마감"
                holder.tvStatusText.setTextColor(Color.parseColor("#757575"))
                setRoundBackground(holder.tvStatusText, "#F5F5F5")

                holder.btnStatus.text = "마감"
                holder.btnStatus.backgroundTintList = ColorStateList.valueOf(Color.parseColor("#BDBDBD"))
            }
        }

        // 버튼 클릭 시 로직 전달
        holder.btnStatus.setOnClickListener { onItemClick(room) }

        // 애니메이션 효과
        setFadeAnimation(holder.itemView)
    }

    override fun getItemCount(): Int = roomList.size

    private fun setRoundBackground(textView: TextView, bgColor: String) {
        val drawable = GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            cornerRadius = 50f // 더 둥글게 조정
            setColor(Color.parseColor(bgColor))
        }
        textView.background = drawable
        textView.setPadding(20, 8, 20, 8) // 가독성을 위한 패딩 추가
    }

    private fun setFadeAnimation(view: View) {
        val anim = AlphaAnimation(0.0f, 1.0f)
        anim.duration = 400
        view.startAnimation(anim)
    }
}