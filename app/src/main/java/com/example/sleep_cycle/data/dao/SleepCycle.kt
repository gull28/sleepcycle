package com.example.sleep_cycle.data.dao

import androidx.room.*
import com.example.sleep_cycle.data.models.SleepTime
import com.example.sleep_cycle.data.models.SleepCycle

@Dao
interface SleepCycleDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    open suspend fun addSleepCycle(sleepCycle: SleepCycle)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    open suspend fun insertSleepTimes(sleepTimes: List<SleepTime>)

    @Transaction
    open suspend fun addSleepCycleWithTimes(sleepCycle: SleepCycle, sleepTimes: List<SleepTime>) {
        val cycleId = addSleepCycle(sleepCycle)
        val timesWithCycleId = sleepTimes.map { it.copy(scheduleId = cycleId) }
        insertSleepTimes(timesWithCycleId)
    }

    @Query("UPDATE sleep_cycles SET isActive = :isActive WHERE id = :id")
    open suspend fun toggleActive(id: Long, isActive: Int)

    @Query("DELETE FROM sleep_times WHERE scheduleId = :scheduleId")
    open suspend fun deleteSleepTimesByScheduleId(scheduleId: Long)

    @Transaction
    open suspend fun saveSleepTimes(cycleId: Long, sleepTimes: List<SleepTime>) {
        deleteSleepTimesByScheduleId(cycleId)
        insertSleepTimes(sleepTimes)
    }

    @Transaction
    open suspend fun toggleActiveCycle(id: Long, isActive: Int) {
        unsetAllActiveCycles()

        if (isActive == 1) {
            toggleActive(id, 1)
        }
    }

    @Query("UPDATE sleep_cycles SET isActive = 0 WHERE isActive = 1")
    suspend fun unsetAllActiveCycles()

    @Query("SELECT * FROM sleep_cycles WHERE id = :id")
    suspend fun getSleepCycleById(id: Long): SleepCycle?

    @Query("SELECT * FROM sleep_times WHERE scheduleId = :scheduleId")
    suspend fun getSleepTimesForCycle(scheduleId: Long): List<SleepTime>

    @Transaction
    suspend fun getSleepCycleWithPopulatedTimes(id: Long): SleepCycle? {
        val sleepCycle = getSleepCycleById(id)
        sleepCycle?.let {
            it.sleepTimes = getSleepTimesForCycle(id)
        }
        return sleepCycle
    }

    @Query("SELECT * FROM sleep_cycles")
    suspend fun getAllSleepCyclesRaw(): List<SleepCycle>

    @Transaction
    suspend fun getAllSleepCyclesWithTimes(): List<SleepCycle> {
        val sleepCycles = getAllSleepCyclesRaw()
        return sleepCycles.map { cycle ->
            cycle.apply {
                sleepTimes = getSleepTimesForCycle(cycle.id)
            }
        }
    }

    @Transaction
    @Query("SELECT * FROM sleep_cycles WHERE isActive = 1")
    suspend fun getActiveSleepCycle(): SleepCycle? {
        val activeSleepCycle = getAllSleepCyclesRaw().find { it.isActive == 1 }
        activeSleepCycle?.let {
            it.sleepTimes = getSleepTimesForCycle(it.id)
        }
        return activeSleepCycle
    }

    @Update
    suspend fun updateSleepCycle(sleepCycle: SleepCycle): Int

    @Transaction
    suspend fun deleteSleepCycle(sleepCycle: SleepCycle) {
        deleteSleepTimesByScheduleId(sleepCycle.id)
        deleteSleepCycleById(sleepCycle.id)
    }

    @Query("DELETE FROM sleep_cycles WHERE id = :id")
    suspend fun deleteSleepCycleById(id: Long)

}
