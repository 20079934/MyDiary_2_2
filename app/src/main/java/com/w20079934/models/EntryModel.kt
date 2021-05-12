package com.w20079934.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class EntryModel (
        var id : Long = 0,
        var date: Map<String, Int> = mapOf("year" to 0, "month" to 0, "day" to 0),
        var topic: String = "",
        var entry: String = "",
        var image: String = "",
): Parcelable {
    override fun toString(): String = topic
}