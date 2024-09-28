package com.example.sleep_cycle.data

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import android.content.Context
import com.example.sleep_cycle.data.dao.SleepCycleDao
import com.example.sleep_cycle.data.dao.SleepTimeDao
import com.example.sleep_cycle.data.models.SleepTime
import com.example.sleep_cycle.data.models.SleepCycle

@Database(entities = [SleepCycle::class, SleepTime::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun sleepCycleDao(): SleepCycleDao
    abstract fun sleepTimeDao(): SleepTimeDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
