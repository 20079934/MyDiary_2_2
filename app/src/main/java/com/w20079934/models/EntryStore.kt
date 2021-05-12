package com.w20079934.models

interface EntryStore {
    fun findAll(): List<EntryModel>
    fun create(entry: EntryModel)
    fun update(entry: EntryModel)
    fun updateName(name: String)
    fun getName(): String
    fun remove(entry: EntryModel)
}