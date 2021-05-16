package com.w20079934.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import com.w20079934.activities.Home
import com.w20079934.helpers.readImage
import com.w20079934.helpers.readImageFromPath
import com.w20079934.helpers.showImagePicker
import com.w20079934.main.DiaryApp
import com.w20079934.models.EntryModel
import com.w20079934.mydiary_2.R
import kotlinx.android.synthetic.main.fragment_entry.*
import kotlinx.android.synthetic.main.fragment_entry.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ReportFragment
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.w20079934.activities.Login
import com.w20079934.api.EntryWrapper
import com.w20079934.utils.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info

class EntryFragment : Fragment(), AnkoLogger, Callback<MutableList<EntryModel>> {

    lateinit var app: DiaryApp
    var entry = EntryModel()
    var edit = false
    val IMAGE_REQUEST = 1
    lateinit var image: ImageView

    lateinit var loader: AlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        app = activity?.application as DiaryApp

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val root = inflater.inflate(R.layout.fragment_entry, container, false)
        activity?.title = getString(R.string.menu_new_entry)
        loader = createLoader(activity!!)

        image = root.entryImage
        root.textView.setText("Dear ${app.diaryName}")

        if (app.getCurrEntry() != null) {
            edit = true
            entry = app.getCurrEntry()!!
            root.entryTopic.setText(entry.topic)
            root.entryEntry.setText(entry.entry)
            root.btnAdd.text = getString(R.string.updateEntry)

            root.removeEntry.setOnClickListener {
                app.entries.remove(entry)
                app.finishEditingEntry()
                activity!!.supportFragmentManager.popBackStack()//seems to remove going back to the previous fragment, but still needs a lot of back presses to exit
                (activity as Home).openFragment(R.id.nav_Diary) // return user back to the diary after submitting it
            }

            if (entry.image != "") {
                root.entryImage.setImageBitmap(readImageFromPath(activity!!, entry.image))
                root.chooseImage.text = getString(R.string.button_changeImage)
            }
        } else {
            root.removeEntry.visibility = View.INVISIBLE
        }


        root.chooseImage.setOnClickListener {
            showImagePicker(activity!!, IMAGE_REQUEST)
        }
        root.btnAdd.setOnClickListener()
        {
            entry.email = app.auth.currentUser?.email!!
            if (entryEntry.text.isNotEmpty()) {
                if (edit) {
                    entry.topic = entryTopic.text.toString()
                    entry.entry = entryEntry.text.toString()

                    updateEntry(entry.id, entry)
                    updateUserEntry(app.auth.currentUser!!.uid, entry!!.id, entry)
                } else {
                    entry.topic = entryTopic.text.toString()
                    entry.entry = entryEntry.text.toString()
                    writeNewEntry(entry)
                    //app.entries.create(entry.copy())
                }

                app.finishEditingEntry()
                activity!!.supportFragmentManager.popBackStack()//seems to remove going back to the previous fragment, but still needs a lot of back presses to exit
                (activity as Home).openFragment(R.id.nav_Diary) // return user back to the diary after submitting it


            } else {
                Toast.makeText(activity!!, getString(R.string.menu_invalidEntry), Toast.LENGTH_LONG)
                    .show()
            }
        }


        return root;
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            EntryFragment().apply {
                arguments = Bundle().apply {}
            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Toast.makeText(activity, "trying?", Toast.LENGTH_SHORT).show()
        when (requestCode) {
            IMAGE_REQUEST -> {
                if (data != null) {
                    entry.image = data.data.toString()
                    image.setImageBitmap(readImage(activity!!, resultCode, data))
                    chooseImage.text = getString(R.string.button_changeImage)
                }
            }
        }
    }

    override fun onFailure(call: Call<MutableList<EntryModel>>, t: Throwable) {
        info("Retrofit Error : $t.message")
        serviceUnavailableMessage(activity!!)
        hideLoader(loader)
    }

    override fun onResponse(
        call: Call<MutableList<EntryModel>>,
        response: Response<MutableList<EntryModel>>
    ) {
        serviceAvailableMessage(activity!!)
        info("Retrofit JSON = $response.raw()")
        app.entries = response.body() as MutableList<EntryModel>
        hideLoader(loader)
    }

    override fun onResume() {
        super.onResume()
        getAllEntries()
    }

    fun getAllEntries() {
        showLoader(loader, "Getting Entries...")
        var callGetAll = app.diaryService.getall()
        callGetAll.enqueue(this)
    }

    fun updateUserEntry(userId: String, uid: String?, entry: EntryModel) {
        app.database.child("user-donations").child(userId).child(uid!!)
            .addListenerForSingleValueEvent(
                object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        snapshot.ref.setValue(entry)
                        activity!!.supportFragmentManager.beginTransaction()
                            .replace(R.id.homeFrame, DiaryFragment.newInstance())
                            .addToBackStack(null)
                            .commit()
                        hideLoader(loader)
                    }

                    override fun onCancelled(error: DatabaseError) {
                        info("Firebase Donation error : ${error.message}")
                    }
                })
    }

    fun updateEntry(uid: String?, entry: EntryModel)
    {
        app.database.child("entries").child(uid!!)
            .addListenerForSingleValueEvent(
                object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        snapshot.ref.setValue(entry)
                        hideLoader(loader)
                    }

                    override fun onCancelled(error: DatabaseError) {
                        info("Firebase Entry error : ${error.message}")
                    }
                })
    }


    fun addEntry(entry : EntryModel) {
        showLoader(loader, "Adding an entry...")
        var callAdd = app.diaryService.post(app.auth.currentUser?.email!!,entry)
        callAdd.enqueue(object : Callback<EntryWrapper> {
            override fun onFailure(call: Call<EntryWrapper>, t: Throwable) {
                info("Retrofit Error : $t.message")
                serviceUnavailableMessage(activity!!)
                hideLoader(loader)
            }

            override fun onResponse(call: Call<EntryWrapper>,
                                    response: Response<EntryWrapper>) {
                val donationWrapper = response.body()
                info("Retrofit Wrapper : $donationWrapper")
                getAllEntries()
                hideLoader(loader)
            }
        })
    }

    fun writeNewEntry(entry: EntryModel) {
        // Create new donation at /donations & /donations/$uid
        showLoader(loader, "Adding Entry to Firebase")
        info("Firebase DB Reference : $app.database")
        val uid = app.auth.currentUser!!.uid
        val key = app.database.child("entries").push().key
        if (key == null) {
            info("Firebase Error : Key Empty")
            return
        }
        entry.id = key
        val entryValues = entry.toMap()

        val childUpdates = HashMap<String, Any>()
        childUpdates["/entries/$key"] = entryValues
        childUpdates["/user-entries/$uid/$key"] = entryValues

        app.database.updateChildren(childUpdates)
        hideLoader(loader)
    }
}