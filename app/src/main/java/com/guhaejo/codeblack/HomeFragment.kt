package com.guhaejo.codeblack

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [HomeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HomeFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var rootView = inflater.inflate(R.layout.fragment_home, container, false)

        var site1_img = rootView.findViewById<ImageView>(R.id.site1_img)
        var site2_img = rootView.findViewById<ImageView>(R.id.site2_img)
        var site3_img = rootView.findViewById<ImageView>(R.id.site3_img)
        var counseling_btn = rootView.findViewById<LinearLayout>(R.id.counseling_btn)

        counseling_btn.setOnClickListener {
            val transaction = requireActivity().supportFragmentManager.beginTransaction()
            val counselingFragment = CounselingFragment.newInstance("","")
//            transaction.addToBackStack(null)
            transaction.replace(R.id.mainFrameLayout, counselingFragment)
            transaction.commit()
        }

        site1_img.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.kdca.go.kr"))
            startActivity(intent)
        }

        site2_img.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.nhis.or.kr"))
            startActivity(intent)
        }

        site3_img.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.hira.or.kr"))
            startActivity(intent)
        }

        return rootView
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            HomeFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}