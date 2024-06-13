package com.guhaejo.codeblack

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.guhaejo.codeblack.databinding.ActivityHomeBinding

private const val TAG_HOME = "ic_home"
private const val TAG_HOSPITAL = "ic_hospital"
private const val TAG_COUNSELINGLIST = "ic_counseling_list"
private const val TAG_MYPAGE = "ic_mypage"

class BottomNavActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.homeBottomNav.itemIconTintList = null
        setFragment(TAG_HOME, HomeFragment(), true) // 초기 로드는 항상 replace로 설정

        binding.homeBottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.ic_home -> setFragment(TAG_HOME, HomeFragment(), true) // 홈 버튼 클릭 시 항상 replace
                R.id.ic_mypage -> setFragment(TAG_MYPAGE, MypageFragment())
                R.id.ic_hospital -> setFragment(TAG_HOSPITAL, HospitalFragment())
                R.id.ic_counseling -> setFragment(TAG_COUNSELINGLIST, CounselingFragment())
            }
            true
        }
    }

    fun setFragment(tag: String, fragment: Fragment, forceReplace: Boolean = false) {
        val manager: FragmentManager = supportFragmentManager
        val fragTransaction = manager.beginTransaction()

        val currentFragment = manager.findFragmentByTag(tag)

        if (currentFragment == null || forceReplace) {
            fragTransaction.replace(R.id.mainFrameLayout, fragment, tag)
        } else {
            manager.fragments.forEach {
                if (it == currentFragment) {
                    fragTransaction.show(it)
                } else {
                    fragTransaction.hide(it)
                }
            }
        }

        fragTransaction.addToBackStack(null)
        fragTransaction.commitAllowingStateLoss()
    }
}
