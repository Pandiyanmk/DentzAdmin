package com.app.dentzadmin.data.model

data class MessageToFirebase(
    val content: String, val questions: ArrayList<Question>
)