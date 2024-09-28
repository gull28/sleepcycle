package com.example.sleep_cycle.data.repository

import com.example.sleep_cycle.data.dao.SleepTimeDao
import com.example.sleep_cycle.data.models.SleepTime

class SleepTimeRepository(private val sleepTimeDao: SleepTimeDao) {

    suspend fun addSleepTime(sleepTime: SleepTime): Long {
        return sleepTimeDao.addSleepTime(sleepTime)
    }

    suspend fun getSleepTimeById(id: Long): SleepTime? {
        return sleepTimeDao.getSleepTimeById(id)
    }

    suspend fun getAllSleepTimes(): List<SleepTime> {
        return sleepTimeDao.getAllSleepTimes()
    }

    suspend fun updateSleepTime(sleepTime: SleepTime): Int {
        return sleepTimeDao.updateSleepTime(sleepTime)
    }

    suspend fun deleteSleepTime(sleepTime: SleepTime): Int {
        return sleepTimeDao.deleteSleepTime(sleepTime)
    }

    suspend fun deleteSleepTimeById(id: Long): Int {
        return sleepTimeDao.deleteSleepTimeById(id)
    }
}
