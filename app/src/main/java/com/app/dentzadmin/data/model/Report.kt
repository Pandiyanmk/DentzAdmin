package com.app.dentzadmin.data.model

data class Report(
    val groupCount: List<GroupCount>,
    val groupid: String,
    val messageData: String,
    val messageid: String
)