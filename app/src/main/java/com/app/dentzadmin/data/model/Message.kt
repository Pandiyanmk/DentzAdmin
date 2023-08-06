package com.app.dentzadmin.data.model

data class Message(
    val contentEnglish: String,
    val contentNepali: String,
    val status: Int = 0,
    val id: String,
    val questionsidEnglish: String,
    val questionsidNepali: String
)