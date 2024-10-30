package com.example.sleep_cycle.dao

import com.example.sleep_cycle.data.dao.SleepTimeDao
import com.example.sleep_cycle.data.models.SleepTime

class MockSleepTimeDao: SleepTimeDao {
    override suspend fun addSleepTime(sleepTime: SleepTime): Long {
        TODO("Not yet implemented")
    }

    override suspend fun getSleepTimeById(id: Long): SleepTime? {
        TODO("Not yet implemented")
    }

    override suspend fun getAllSleepTimes(): List<SleepTime> {
        TODO("Not yet implemented")
    }

    override suspend fun updateSleepTime(sleepTime: SleepTime): Int {
        TODO("Not yet implemented")
    }

    override suspend fun deleteSleepTime(sleepTime: SleepTime): Int {
        TODO("Not yet implemented")
    }

    override suspend fun deleteSleepTimeById(id: Long): Int {
        TODO("Not yet implemented")
    }
}