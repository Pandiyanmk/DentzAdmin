package com.app.dentzadmin.data.model

data class MessageToFirebaseRead(
    val content: String? = null, val questions: List<QuestionRead>? = null
) {
    constructor() : this("", emptyList())
}
