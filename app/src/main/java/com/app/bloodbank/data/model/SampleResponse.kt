package com.app.bloodbank.data.model

data class SampleResponse(
    val messageText: String,
    val audio: String,
    val video: String,
    val password: String,
    val questions: List<Question>,
    val userId: String
)