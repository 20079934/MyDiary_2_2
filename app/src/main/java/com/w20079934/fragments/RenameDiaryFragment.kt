package com.w20079934.fragments

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.w20079934.activities.Home
import com.w20079934.adapters.EntryAdapter
import com.w20079934.main.DiaryApp
import com.w20079934.models.EntryModel
import com.w20079934.mydiary_2.R
import kotlinx.android.synthetic.main.fragment_diary.view.*
import kotlinx.android.synthetic.main.fragment_renamediary.*
import kotlinx.android.synthetic.main.fragment_renamediary.view.*

class RenameDiaryFragment : Fragment() {
    lateinit var app: DiaryApp

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        app = activity?.application as DiaryApp
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val root = inflater.inflate(R.layout.fragment_renamediary, container, false)
        activity?.title = "Rename Diary"
        root.submitName.setOnClickListener {
            if(diaryName.text.toString()!="") {
            app.diaryName = diaryName.text.toString()
            activity!!.supportFragmentManager.popBackStack()//seems to remove going back to the previous fragment, but still needs a lot of back presses to exit
            (activity as Home).openFragment(R.id.nav_Diary) // return user back to the diary after submitting it
            }
            else {
                Toast.makeText(activity, "Name is not valid", Toast.LENGTH_SHORT).show()
            }
        }
        return root;
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            RenameDiaryFragment().apply {
                arguments = Bundle().apply {}
            }

    }

}