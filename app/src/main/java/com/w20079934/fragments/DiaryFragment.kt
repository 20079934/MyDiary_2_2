package com.w20079934.fragments

import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.w20079934.activities.Home
import com.w20079934.activities.Login
import com.w20079934.adapters.EntryAdapter
import com.w20079934.adapters.EntryListener
import com.w20079934.main.DiaryApp
import com.w20079934.models.EntryModel
import com.w20079934.mydiary_2.R
import com.w20079934.utils.*
import kotlinx.android.synthetic.main.fragment_diary.view.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DiaryFragment : Fragment(), EntryListener, Callback<MutableList<EntryModel>>, AnkoLogger {
    lateinit var app: DiaryApp

    lateinit var loader : AlertDialog
    lateinit var root: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        app = activity?.application as DiaryApp
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        root = inflater.inflate(R.layout.fragment_diary, container, false)
        loader = createLoader(activity!!)
        activity?.title = "Hello ${app.diaryName}!"

        root.recyclerView.setLayoutManager(LinearLayoutManager(activity))
        root.recyclerView.adapter = EntryAdapter(app.entries, this)

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

    override fun onFailure(call: Call<MutableList<EntryModel>>, t: Throwable) {
        info("Retrofit Error : $t.message")
        serviceUnavailableMessage(activity!!)
        hideLoader(loader)
    }

    override fun onResponse(call: Call<MutableList<EntryModel>>,
                            response: Response<MutableList<EntryModel>>
    ) {
        serviceAvailableMessage(activity!!)
        info("Retrofit JSON = ${response.body()}")
        app.entries = response.body() as MutableList<EntryModel>
        root.recyclerView.adapter = EntryAdapter(app.entries, this)
        root.recyclerView.adapter?.notifyDataSetChanged()
        hideLoader(loader)
    }

    fun getAllEntries() {
        showLoader(loader, "Downloading Entries...")
        var callGetAll = app.diaryService.getall()
        callGetAll.enqueue(this)
    }

    override fun onResume() {
        super.onResume()
        getAllEntries(app.auth.currentUser!!.uid)
    }

    fun getAllEntries(userId: String?) {
        showLoader(loader, "Downloading Donations from Firebase")
        var entryList = ArrayList<EntryModel>()
        app.database.child("user-entries").child(userId!!)
            .addValueEventListener(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                    info("Firebase Entry error : ${error.message}")
                }

                override fun onDataChange(snapshot: DataSnapshot) {
                    val children = snapshot!!.children
                    children.forEach {
                        val entry = it.getValue<EntryModel>(EntryModel::class.java!!)

                        entryList.add(entry!!)
                        app.entries = entryList
                        root.recyclerView.adapter =
                            EntryAdapter(app.entries, this@DiaryFragment)
                        root.recyclerView.adapter?.notifyDataSetChanged()
                        hideLoader(loader)
                        app.database.child("user-donations").child(userId!!).removeEventListener(this)
                    }
                }
            })
    }

}