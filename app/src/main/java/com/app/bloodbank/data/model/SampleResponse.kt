package com.app.bloodbank.data.model

data class SampleResponse(
    val messageText: String,
    val audio: String,
    val video: String,
    val password: String,
    val group: ArrayList<Group>,
    val questions: List<Question>,
    val message: ArrayList<Message>,
    val maxselect: Int,
    val userId: String
)