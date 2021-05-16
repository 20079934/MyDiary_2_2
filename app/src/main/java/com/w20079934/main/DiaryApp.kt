package com.w20079934.main


import EntryJSONStore
import android.app.Application
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.w20079934.api.DiaryService
import com.w20079934.models.EntryModel
import com.w20079934.models.EntryStore

class DiaryApp : Application() {
    lateinit var entries : MutableList<EntryModel>
    lateinit var diaryService: DiaryService
    var diaryName = "World"
    private var currEntry : EntryModel? = null

    lateinit var auth: FirebaseAuth


    lateinit var database : DatabaseReference

    override fun onCreate() {
        super.onCreate()
        diaryService = DiaryService.create()
    }

    fun editEntry(entry : EntryModel)
    {
        currEntry = entry
    }

    fun getCurrEntry() : EntryModel?
    {
        return currEntry
    }

    fun finishEditingEntry()
    {
        currEntry = null
    }


}