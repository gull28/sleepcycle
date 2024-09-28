package com.example.sleep_cycle.data.dao

import androidx.room.*
import com.example.sleep_cycle.data.models.SleepTime

@Dao
interface SleepTimeDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addSleepTime(sleepTime: SleepTime): Long

    @Query("SELECT * FROM sleep_times WHERE id = :id")
    suspend fun getSleepTimeById(id: Long): SleepTime?

    @Query("SELECT * FROM sleep_times")
    suspend fun getAllSleepTimes(): List<SleepTime>

    @Update
    suspend fun updateSleepTime(sleepTime: SleepTime): Int

    @Delete
    suspend fun deleteSleepTime(sleepTime: SleepTime): Int

    @Query("DELETE FROM sleep_times WHERE id = :id")
    suspend fun deleteSleepTimeById(id: Long): Int
}
