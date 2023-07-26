package com.app.dentzadmin.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "send_data")
data class SendData(
    @PrimaryKey(autoGenerate = true) val id: Int = 0, val message: String, var groups: String
)
