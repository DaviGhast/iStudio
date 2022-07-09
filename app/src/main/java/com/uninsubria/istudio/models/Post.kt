package com.uninsubria.istudio.models

class Post(
    val id: String,
    val title: String,
    val text: String,
    val fromId: String,
    val timestamp: Long
) {
    constructor() : this("", "", "", "", -1)
}