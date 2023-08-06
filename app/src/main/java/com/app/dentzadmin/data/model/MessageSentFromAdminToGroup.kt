package com.app.dentzadmin.data.model

data class MessageSentFromAdminToGroup(
    val content: String, val groups: String
) {
    constructor() : this("", "")
}