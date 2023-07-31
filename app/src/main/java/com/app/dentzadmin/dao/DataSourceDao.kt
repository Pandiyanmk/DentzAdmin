package com.app.dentzadmin.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.app.dentzadmin.data.model.SendData


@Dao
interface DataSourceDao {
    @Query("SELECT * FROM send_data WHERE message =:message")
    fun getData(message: String): List<SendData>

    @Query("SELECT * FROM send_data")
    fun getAllData(): List<SendData>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertData(dataSources: SendData)

    @Query("SELECT * FROM send_data WHERE message =:message")
    suspend fun getMessageData(message: String): SendData

    @Update
    fun updateData(tour: SendData?): Int

    @Query("DELETE FROM send_data")
    fun nukeTable()

    suspend fun updateGroup(message: String, groupName: String) {
        val ds: SendData = getMessageData(message)
        ds.groups = groupName
        updateData(ds)
    }
}