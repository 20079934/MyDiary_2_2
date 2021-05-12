package com.w20079934.fragments

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.w20079934.activities.Home
import com.w20079934.adapters.EntryAdapter
import com.w20079934.adapters.EntryListener
import com.w20079934.main.DiaryApp
import com.w20079934.models.EntryModel
import com.w20079934.mydiary_2.R
import kotlinx.android.synthetic.main.fragment_diary.view.*

class DiaryFragment : Fragment(), EntryListener {
    lateinit var app: DiaryApp

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        app = activity?.application as DiaryApp
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val root = inflater.inflate(R.layout.fragment_diary, container, false)
        activity?.title = "Hello ${app.entries.getName()}!"

        root.recyclerView.setLayoutManager(LinearLayoutManager(activity))
        root.recyclerView.adapter = EntryAdapter(app.entries.findAll(), this)

        return root;
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            DiaryFragment().apply {
                arguments = Bundle().apply {}
            }

    }

    fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        activity?.menuInflater?.inflate(R.menu.menu_diary, menu)
        return true
    }

    override fun onEntryClick(entry: EntryModel) {
        app.editEntry(entry)
        activity!!.supportFragmentManager.popBackStack()//seems to remove going back to the previous fragment, but still needs a lot of back presses to exit
        (activity as Home).openFragment(R.id.nav_newEntry) // return user back to the diary after submitting it
    }
}