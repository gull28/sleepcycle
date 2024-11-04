package com.example.sleep_cycle.mocks.dao

import com.example.sleep_cycle.data.dao.SleepTimeDao
import com.example.sleep_cycle.data.models.SleepTime

class MockSleepTimeDao : SleepTimeDao {

    private val sleepTimes = mutableListOf<SleepTime>()
    private var idCounter = 1L

    override suspend fun addSleepTime(sleepTime: SleepTime): Long {
        sleepTime.id = idCounter++ // Assign a unique ID to the SleepTime
        sleepTimes.add(sleepTime)
        return sleepTime.id!!
    }

    override suspend fun getSleepTimeById(id: Long): SleepTime? {
        return sleepTimes.find { it.id == id }
    }

    override suspend fun getAllSleepTimes(): List<SleepTime> {
        return sleepTimes.toList() // Return a copy of the list to prevent external modification
    }

    override suspend fun updateSleepTime(sleepTime: SleepTime): Int {
        val index = sleepTimes.indexOfFirst { it.id == sleepTime.id }
        return if (index != -1) {
            sleepTimes[index] = sleepTime
            1 // Return 1 to indicate a successful update
        } else {
            0 // Return 0 if no matching SleepTime was found
        }
    }

    override suspend fun deleteSleepTime(sleepTime: SleepTime): Int {
        return if (sleepTimes.remove(sleepTime)) {
            1 // Return 1 if the item was successfully removed
        } else {
            0 // Return 0 if the item was not found
        }
    }

    override suspend fun deleteSleepTimeById(id: Long): Int {
        val itemToRemove = sleepTimes.find { it.id == id }
        return if (itemToRemove != null && sleepTimes.remove(itemToRemove)) {
            1 // Return 1 if the item was successfully removed
        } else {
            0 // Return 0 if the item was not found
        }
    }
}
