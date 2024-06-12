package com.guhaejo.codeblack
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import androidx.fragment.app.Fragment

class CounselinglistFragment : Fragment() {

    // Sample data for the counseling list
    private val counselingList = arrayOf(
        "Counseling 1",
        "Counseling 2",
        "Counseling 3",
        "Counseling 4",
        "Counseling 5"
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_counselinglist, container, false)
    }


}
