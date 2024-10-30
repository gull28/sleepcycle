package com.example.sleep_cycle.repository

import com.example.sleep_cycle.data.dao.SleepCycleDao
import com.example.sleep_cycle.data.repository.SleepCycleRepository

class FakeSleepCycleRepository(
    sleepCycleDao: SleepCycleDao

): SleepCycleRepository(sleepCycleDao) {
}