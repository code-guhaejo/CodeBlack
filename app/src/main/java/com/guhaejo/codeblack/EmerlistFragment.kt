package com.guhaejo.codeblack

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.TextView
import androidx.fragment.app.Fragment

class EmerlistFragment : Fragment() {


    private var category: String?=null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // 올바른 레이아웃 파일을 인플레이트합니다.
        return inflater.inflate(R.layout.fragment_emerlist, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // ListView의 ID가 counseling_list_view인지 확인


      /*  val counselingBtn: LinearLayout = view.findViewById(R.id.counseling_btn)
        counselingBtn.setOnClickListener {
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            transaction.replace(R.id.mainFrameLayout, CounselingFragment())
            transaction.addToBackStack(null)
            transaction.commit()
        }
        */
        /*val listView: ListView? = view.findViewById(R.id.counseling_list_view)

        if (listView == null) {
            Log.e("EmerlistFragment", "ListView is null, check the layout file.")
        } else {
            // 더미 데이터 생성
            val dummyData = listOf(
                "충북대학교병원 응급의료센터",
                "하나병원",
                //"충청북도 청주의료원",
                "마이크로병원",
                "청주현대병원",
                //"청주 소아청소년병원",
                "한국병원",
                "효성병원"
            )

            // ArrayAdapter 생성
            val adapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_list_item_1,
                dummyData
            )

            // ListView에 어댑터 설정
            listView.adapter = adapter
        }*/

    }

    companion object {
        fun newInstance(param1: String, param2: String): EmerlistFragment {
            val fragment = EmerlistFragment()
            fragment.category = param1
            return fragment
            return EmerlistFragment()
        }
    }
}
