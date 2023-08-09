package com.app.dentzadmin.data.model

data class FCMResponse(
    val canonical_ids: Int,
    val failure: Int,
    val multicast_id: Long,
    val results: List<Result>,
    val success: Int
)