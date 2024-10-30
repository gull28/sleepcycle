package com.example.sleep_cycle.dao

import com.example.sleep_cycle.data.dao.SleepCycleDao
import com.example.sleep_cycle.data.models.SleepCycle
import com.example.sleep_cycle.data.models.SleepTime

class MockSleepCycleDao : SleepCycleDao {
    private val sleepCycles = mutableListOf<SleepCycle>()

    override suspend fun addSleepCycle(sleepCycle: SleepCycle) {
        sleepCycles.add(sleepCycle)
    }

    override suspend fun insertSleepTimes(sleepTimes: List<SleepTime>) {
        TODO("Not yet implemented")
    }

    override suspend fun toggleActive(id: Long, isActive: Int) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteSleepTimesByScheduleId(scheduleId: Long) {
        TODO("Not yet implemented")
    }

    override suspend fun unsetAllActiveCycles() {
        TODO("Not yet implemented")
    }

    override suspend fun getSleepCycleById(id: Long): SleepCycle? {
        TODO("Not yet implemented")
    }

    override suspend fun getSleepTimesForCycle(scheduleId: Long): List<SleepTime> {
        TODO("Not yet implemented")
    }

    override suspend fun getAllSleepCyclesRaw(): List<SleepCycle> {
        TODO("Not yet implemented")
    }

    override suspend fun updateSleepCycle(sleepCycle: SleepCycle): Int {
        TODO("Not yet implemented")
    }

    override suspend fun deleteSleepCycle(sleepCycle: SleepCycle) {
        sleepCycles.remove(sleepCycle)
    }

    override suspend fun deleteSleepCycleById(id: Long) {
        TODO("Not yet implemented")
    }
}
