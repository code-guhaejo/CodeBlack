package com.guhaejo.codeblack.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.guhaejo.codeblack.HospitalFragment
import com.guhaejo.codeblack.R

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

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
}
