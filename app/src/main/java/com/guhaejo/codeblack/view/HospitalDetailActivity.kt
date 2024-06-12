package com.guhaejo.codeblack.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.TextView
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.guhaejo.codeblack.R

class HospitalDetailActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var map: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_maps)

        // 인텐트로부터 병원 이름, 위도, 경도, 주소, 전화번호 받기
        val hospitalName = intent.getStringExtra("HOSPITAL_NAME")
        val latitude = intent.getDoubleExtra("LATITUDE", 0.0)
        val longitude = intent.getDoubleExtra("LONGITUDE", 0.0)
        val address = intent.getStringExtra("ADDRESS")
        val phoneNumber = intent.getStringExtra("PHONE_NUMBER")

        // SupportMapFragment를 가져와서 맵을 준비한다
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        // 병원 정보 설정
        findViewById<TextView>(R.id.hospital_detail).text ="""
            병원 이름: $hospitalName
            주소: $address
            전화번호: $phoneNumber
        """.trimIndent()
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap

        // 병원의 위치 설정
        val hospitalLocation = LatLng(intent.getDoubleExtra("LATITUDE", 0.0), intent.getDoubleExtra("LONGITUDE", 0.0))
        map.addMarker(MarkerOptions().position(hospitalLocation).title(intent.getStringExtra("HOSPITAL_NAME")))
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(hospitalLocation, 15f))
    }
}