package com.guhaejo.codeblack.view

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.guhaejo.codeblack.HospitalFragment
import com.guhaejo.codeblack.R

class MainActivity : AppCompatActivity() {

    companion object {
        private const val REQUEST_CALL_PHONE = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 전화 권한 요청
        requestCallPermission()

        // 초기 프래그먼트 로드
        if (savedInstanceState == null) {
            loadFragment(HospitalFragment())
        }
    }

    private fun loadFragment(fragment: Fragment) {
        // 프래그먼트를 로드하는 함수
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_container, fragment)
        transaction.commit()
    }

    private fun requestCallPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CALL_PHONE), REQUEST_CALL_PHONE)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CALL_PHONE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 권한이 허용된 경우
            } else {
                // 권한이 거부된 경우
            }
        }
    }
}