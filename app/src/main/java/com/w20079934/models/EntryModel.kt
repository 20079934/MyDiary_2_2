package com.w20079934.models

import android.os.Parcelable
import com.google.firebase.database.Exclude
import kotlinx.android.parcel.Parcelize

@Parcelize
data class EntryModel (
        var id : String = "0",
        var date: Map<String, Int> = mapOf("year" to 0, "month" to 0, "day" to 0),
        var topic: String = "",
        var entry: String = "",
        var image: String = "",
        var email: String = "test@test.com"
): Parcelable {
    override fun toString(): String = topic


        @Exclude
        fun toMap(): Map<String, Any?> {
                return mapOf(
                        "id" to id,
                        "date" to date,
                        "topic" to topic,
                        "entry" to entry,
                        "image" to image,
                        "email" to email
                )
        }
}