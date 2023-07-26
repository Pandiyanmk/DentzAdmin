package com.app.dentzadmin.data.model

data class Message(
    val content: String, val status: Int = 0, val questions: ArrayList<Question>
)