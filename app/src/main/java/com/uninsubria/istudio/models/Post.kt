package com.uninsubria.istudio.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


@Parcelize
class Post(
    val id: String,
    var title: String,
    val text: String,
    val fromId: String,
    val timestamp: Long,
    val latitude: String,
    val longitude: String
): Parcelable {
    constructor() : this("", "", "", "", -1,"","")
}