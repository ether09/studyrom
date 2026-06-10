package com.example.couplemap

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*

class MainActivity : AppCompatActivity() {

    private val database = FirebaseDatabase.getInstance()
    private val myRef = database.getReference("places")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val etPlaceName = findViewById<EditText>(R.id.et_place_name)
        val btnAdd = findViewById<Button>(R.id.btn_add)
        val rvPlaceList = findViewById<RecyclerView>(R.id.rv_place_list)

        val placeList = mutableListOf<Place>()

        // 어댑터 설정
        val adapter = PlaceAdapter(placeList) { place ->
            val intent = Intent(this, MapActivity::class.java)
            intent.putExtra("name", place.name)
            intent.putExtra("lat", place.latitude)
            intent.putExtra("lng", place.longitude)
            startActivity(intent)
        }

        rvPlaceList.layoutManager = LinearLayoutManager(this)
        rvPlaceList.adapter = adapter

        // 파이어베이스 데이터 연동
        myRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                placeList.clear()
                for (item in snapshot.children) {
                    val place = item.getValue(Place::class.java)
                    if (place != null) placeList.add(place)
                }
                adapter.notifyDataSetChanged()
            }
            override fun onCancelled(error: DatabaseError) {}
        })

        // 버튼 클릭 이벤트
        btnAdd.setOnClickListener {
            val name = etPlaceName.text.toString()
            if (name.isNotEmpty()) {
                val newPlace = Place(name = name, latitude = 35.1796, longitude = 129.0756)
                myRef.push().setValue(newPlace)
                etPlaceName.text.clear()
            }
        }
    }
}