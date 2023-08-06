package com.app.dentzadmin.data.model

data class UserGroupMessages(
    val messageid: ArrayList<MessageId>,
    val message: ArrayList<Message>,
    val questions: ArrayList<Question>
)