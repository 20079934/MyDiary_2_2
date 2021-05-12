package com.w20079934.main


import EntryJSONStore
import android.app.Application
import com.w20079934.models.EntryModel
import com.w20079934.models.EntryStore

class DiaryApp : Application() {
    lateinit var entries : EntryStore
    private var currEntry : EntryModel? = null



    override fun onCreate() {
        super.onCreate()
        entries = EntryJSONStore(applicationContext)


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