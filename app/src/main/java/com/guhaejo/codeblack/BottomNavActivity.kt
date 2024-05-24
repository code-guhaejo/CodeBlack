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
        setFragment(TAG_HOME, HomeFragment())

        binding.homeBottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.ic_home -> setFragment(TAG_HOME, HomeFragment())
                R.id.ic_mypage -> setFragment(TAG_MYPAGE, MypageFragment())
                R.id.ic_hospital -> setFragment(TAG_HOSPITAL, HospitalFragment())
                R.id.ic_counseling -> setFragment(TAG_COUNSELINGLIST, CounselinglistFragment())
            }
                true
            }
        }

    fun setFragment(tag: String, fragment: Fragment) {
        val manager: FragmentManager = supportFragmentManager
        val fragTransaction = manager.beginTransaction()

        if (manager.findFragmentByTag(tag) == null){
            fragTransaction.add(R.id.mainFrameLayout, fragment, tag)
        }

        val home = manager.findFragmentByTag(TAG_HOME)
        val mypage = manager.findFragmentByTag(TAG_MYPAGE)
        val counselinglist = manager.findFragmentByTag(TAG_COUNSELINGLIST)
        val hospital = manager.findFragmentByTag(TAG_HOSPITAL)


        if (home != null) {
            fragTransaction.hide(home)
        }

        if (counselinglist != null) {
            fragTransaction.hide(counselinglist)
        }

        if (hospital != null) {
            fragTransaction.hide(hospital)
        }

        if (mypage != null) {
            fragTransaction.hide(mypage)
        }

        if (tag == TAG_HOME) {
            if (home != null) {
                fragTransaction.show(home)
            }
        }
        else if (tag == TAG_MYPAGE) {
            if (mypage != null) {
                fragTransaction.show(mypage)
            }
        }
        else if (tag == TAG_HOSPITAL) {
            if (hospital != null) {
                fragTransaction.show(hospital)
            }
        }
        else if (tag == TAG_COUNSELINGLIST) {
            if (counselinglist != null) {
                fragTransaction.show(counselinglist)
            }
        }

        fragTransaction.commitAllowingStateLoss()
    }
}