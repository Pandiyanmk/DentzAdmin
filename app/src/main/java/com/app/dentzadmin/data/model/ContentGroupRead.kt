package com.app.dentzadmin.data.model

data class ContentGroupRead(
    val content: String, val groups: String
) {
    constructor() : this("", "")
}