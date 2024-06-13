package com.guhaejo.codeblack.view

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.guhaejo.codeblack.R

class HospitalDetailActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var map: GoogleMap
    private lateinit var phoneNumber: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_maps)

        // 인텐트로부터 병원 이름, 위도, 경도, 주소, 전화번호, 영업 시간 받기
        val hospitalName = intent.getStringExtra("HOSPITAL_NAME")
        val latitude = intent.getDoubleExtra("LATITUDE", 0.0)
        val longitude = intent.getDoubleExtra("LONGITUDE", 0.0)
        val address = intent.getStringExtra("ADDRESS")
        phoneNumber = intent.getStringExtra("PHONE_NUMBER") ?: ""
        val openingHours = intent.getStringExtra("OPENING_HOURS")

        // SupportMapFragment를 가져와서 맵을 준비한다
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        // 병원 정보 설정
        val hospitalDetails = StringBuilder().apply {
            appendLine("병원 이름: $hospitalName")
            if (!address.isNullOrBlank()) {
                appendLine("주소: $address")
            }
            if (!phoneNumber.isNullOrBlank()) {
                appendLine("전화번호: $phoneNumber")
            }
            if (!openingHours.isNullOrBlank()) {
                appendLine("영업 시간:")
                appendLine(openingHours)
            }
        }.toString()

        findViewById<TextView>(R.id.hospital_detail).text = hospitalDetails

        // 전화 예약 버튼 설정
        findViewById<Button>(R.id.call).setOnClickListener {
            makePhoneCall()
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap

        // 병원의 위치 설정
        val hospitalLocation = LatLng(intent.getDoubleExtra("LATITUDE", 0.0), intent.getDoubleExtra("LONGITUDE", 0.0))
        map.addMarker(MarkerOptions().position(hospitalLocation).title(intent.getStringExtra("HOSPITAL_NAME")))
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(hospitalLocation, 15f))
    }

    private fun makePhoneCall() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
            val callIntent = Intent(Intent.ACTION_CALL)
            callIntent.data = Uri.parse("tel:$phoneNumber")
            startActivity(callIntent)
        } else {
            Toast.makeText(this, "전화 권한이 필요합니다.", Toast.LENGTH_SHORT).show()
        }
    }
}