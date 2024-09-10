package com.example.sleep_cycle.data.db

import com.example.sleep_cycle.data.models.SleepCycle
import com.example.sleep_cycle.data.model.SleepTime
import com.example.sleep_cycle.data.repository.SleepCycleRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class Seed(private val repository: SleepCycleRepository) {

    fun seedDatabase() {
        CoroutineScope(Dispatchers.IO).launch {
            val sleepCycles = listOf(
                SleepCycle(
                    name = "Uberman",
                    isActive = 0,
                    sleepTimes = listOf(
                        SleepTime(name = "Nap 1", startTime = "00:00", duration = 20, scheduleId = 0),
                        SleepTime(name = "Nap 2", startTime = "04:00", duration = 20, scheduleId = 0),
                        SleepTime(name = "Nap 3", startTime = "08:00", duration = 20, scheduleId = 0),
                        SleepTime(name = "Nap 4", startTime = "12:00", duration = 20, scheduleId = 0),
                        SleepTime(name = "Nap 5", startTime = "16:00", duration = 20, scheduleId = 0),
                        SleepTime(name = "Nap 6", startTime = "20:00", duration = 20, scheduleId = 0)
                    )
                ),
                SleepCycle(
                    name = "Biphasic",
                    isActive = 0,
                    sleepTimes = listOf(
                        SleepTime(name = "Main Sleep", startTime = "23:00", duration = 240, scheduleId = 0),
                        SleepTime(name = "Nap", startTime = "14:00", duration = 90, scheduleId = 0)
                    )
                ),
                SleepCycle(
                    name = "Everyman",
                    isActive = 0,
                    sleepTimes = listOf(
                        SleepTime(name = "Core Sleep", startTime = "01:00", duration = 180, scheduleId = 0),
                        SleepTime(name = "Nap 1", startTime = "06:00", duration = 20, scheduleId = 0),
                        SleepTime(name = "Nap 2", startTime = "12:00", duration = 20, scheduleId = 0),
                        SleepTime(name = "Nap 3", startTime = "18:00", duration = 20, scheduleId = 0)
                    )
                ),
                SleepCycle(
                    name = "Polyphasic",
                    isActive = 0,
                    sleepTimes = listOf(
                        SleepTime(name = "Nap 1", startTime = "00:00", duration = 90, scheduleId = 0),
                        SleepTime(name = "Nap 2", startTime = "08:00", duration = 90, scheduleId = 0),
                        SleepTime(name = "Nap 3", startTime = "16:00", duration = 90, scheduleId = 0)
                    )
                ),
                SleepCycle(
                    name = "Monophasic",
                    isActive = 0,
                    sleepTimes = listOf(
                        SleepTime(name = "Night Sleep", startTime = "23:00", duration = 480, scheduleId = 0)
                    )
                )
            )

            sleepCycles.forEach { sleepCycle ->
                repository.addSleepCycle(sleepCycle)
            }
        }
    }
}
