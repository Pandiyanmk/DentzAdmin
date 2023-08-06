package com.app.dentzadmin.data.model

data class GroupMessages(
    val group: ArrayList<Group>,
    val message: ArrayList<Message>,
    val questions: ArrayList<Question>
)