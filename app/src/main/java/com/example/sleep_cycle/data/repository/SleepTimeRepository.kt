package com.example.sleep_cycle.data.repository

import com.example.sleep_cycle.data.dao.SleepTimeDao
import com.example.sleep_cycle.data.models.SleepTime

open class SleepTimeRepository(private val sleepTimeDao: SleepTimeDao) {

    open suspend fun addSleepTime(sleepTime: SleepTime) {
        sleepTimeDao.addSleepTime(sleepTime)
    }

    suspend fun getSleepTimeById(id: Long): SleepTime? {
        return sleepTimeDao.getSleepTimeById(id)
    }

    suspend fun getAllSleepTimes(): List<SleepTime> {
        return sleepTimeDao.getAllSleepTimes()
    }

    open suspend fun updateSleepTime(sleepTime: SleepTime): Int {
        return sleepTimeDao.updateSleepTime(sleepTime)
    }

    suspend fun deleteSleepTime(sleepTime: SleepTime): Int {
        return sleepTimeDao.deleteSleepTime(sleepTime)
    }

    open suspend fun deleteSleepTimeById(id: Long) {
        sleepTimeDao.deleteSleepTimeById(id)
    }
}
