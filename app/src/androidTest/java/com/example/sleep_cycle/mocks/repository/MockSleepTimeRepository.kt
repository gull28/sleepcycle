package com.example.sleep_cycle.mocks.repository

import com.example.sleep_cycle.data.dao.SleepTimeDao
import com.example.sleep_cycle.data.models.SleepTime
import com.example.sleep_cycle.data.repository.SleepTimeRepository

class FakeSleepTimeRepository(sleepTimeDao: SleepTimeDao) : SleepTimeRepository(sleepTimeDao) {
    private val sleepTimes = mutableListOf<SleepTime>()

    override suspend fun addSleepTime(sleepTime: SleepTime) {
        sleepTimes.add(sleepTime)
    }

    override suspend fun deleteSleepTimeById(id: Long) {
        sleepTimes.removeIf { it.id == id }
    }

    override suspend fun updateSleepTime(sleepTime: SleepTime): Int {
        val index = sleepTimes.indexOfFirst { it.id == sleepTime.id }
        return if (index != -1) {
            sleepTimes[index] = sleepTime
            1
        } else {
            0
        }
    }
}
