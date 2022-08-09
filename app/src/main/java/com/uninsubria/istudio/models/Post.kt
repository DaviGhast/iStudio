package com.uninsubria.istudio.models

class Post(
    val id: String,
    var title: String,
    val text: String,
    val fromId: String,
    val timestamp: Long,
    val latitude: String,
    val longitude: String
) {
    constructor() : this("", "", "", "", -1,"","")
}