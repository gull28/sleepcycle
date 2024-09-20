package com.example.sleep_cycle.data

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import com.example.sleep_cycle.data.model.SleepTime

class SleepTimeDatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        Log.d("SleepTimeDatabaseHelper", "Creating tables...")

        val createSleepCyclesTable = """
            CREATE TABLE $TABLE_SLEEP_CYCLES (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_NAME TEXT NOT NULL,
                $COLUMN_ISACTIVE TEXT NOT NULL DEFAULT 0
            );
        """.trimIndent()
        db.execSQL(createSleepCyclesTable)

        val createSleepTimesTable = """
            CREATE TABLE $TABLE_SLEEP_TIMES (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_NAME TEXT NOT NULL,
                $COLUMN_SCHEDULE_ID INTEGER NOT NULL,
                $COLUMN_START_TIME TEXT,
                $COLUMN_DURATION INTEGER,
                FOREIGN KEY($COLUMN_SCHEDULE_ID) REFERENCES $TABLE_SLEEP_CYCLES($COLUMN_ID) ON DELETE CASCADE
            );
        """.trimIndent()
        db.execSQL(createSleepTimesTable)

        Log.d("SleepTimeDatabaseHelper", "Tables created successfully.")
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
//        if(oldVersion < 2){
//            val createSleepTimesTable = """
//
//            );
//        """.trimIndent()
//        }
    }

    companion object {
        private const val DATABASE_NAME = "sleep_cycle.db"
        private const val DATABASE_VERSION = 3

        const val TABLE_SLEEP_CYCLES = "SleepCycles"
        const val TABLE_SLEEP_TIMES = "SleepTimes"

        const val COLUMN_ID = "id"
        const val COLUMN_NAME = "name"

        const val COLUMN_SCHEDULE_ID = "scheduleId"
        const val COLUMN_START_TIME = "startTime"
        const val COLUMN_DURATION = "duration"
        const val COLUMN_ISACTIVE = "isActive"
    }
}
