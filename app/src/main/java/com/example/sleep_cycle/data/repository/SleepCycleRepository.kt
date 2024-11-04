package com.example.sleep_cycle.data.repository

import android.util.Log
import com.example.sleep_cycle.data.dao.SleepCycleDao
import com.example.sleep_cycle.data.models.SleepTime
import com.example.sleep_cycle.data.models.SleepCycle

open class SleepCycleRepository(private val sleepCycleDao: SleepCycleDao) {

    suspend fun addSleepCycle(sleepCycle: SleepCycle) {
        sleepCycleDao.addSleepCycle(sleepCycle)
    }

    suspend fun getSleepCycleById(id: Long): SleepCycle? {
        return sleepCycleDao.getSleepCycleById(id)
    }

    suspend fun getAllSleepCycles(): List<SleepCycle> {
        return sleepCycleDao.getAllSleepCyclesWithTimes()
    }

    suspend fun addSleepCycleWithTimes(sleepCycle: SleepCycle) {
        sleepCycleDao.addSleepCycleWithTimes(sleepCycle, sleepCycle.sleepTimes)
    }

    suspend fun getActiveSleepCycle(): SleepCycle? {
        return sleepCycleDao.getActiveSleepCycle()
    }

    suspend fun toggleActive(id: Long, isActive: Int) {
        sleepCycleDao.toggleActiveCycle(id, isActive)
    }

    suspend fun deleteSleepCycle(sleepCycle: SleepCycle) {
        sleepCycleDao.deleteSleepCycle(sleepCycle)
    }

    suspend fun deleteSleepCycleById(id: Long) {
        try {
            Log.d("SleepCycleRepo", "Attempting to delete SleepCycle with ID: $id")
            val affectedRows = sleepCycleDao.deleteSleepCycleById(id)

            Log.d("24853", affectedRows.toString())
        } catch (e: Exception) {
            Log.e("SleepCycleRepo", "Error deleting SleepCycle: ${e.message}", e)
        }
    }


    suspend fun updateSleepCycle(sleepCycle: SleepCycle): Int {
        return sleepCycleDao.updateSleepCycle(sleepCycle)
    }

    suspend fun getSleepTimesForCycle(scheduleId: Long): List<SleepTime> {
        return sleepCycleDao.getSleepTimesForCycle(scheduleId)
    }

    suspend fun saveSleepTimes(cycleId: Long, sleepTimes: List<SleepTime>) {
        sleepCycleDao.saveSleepTimes(cycleId, sleepTimes)
    }
}
