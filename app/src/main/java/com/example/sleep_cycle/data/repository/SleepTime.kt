package com.example.sleep_cycle.data.repository

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.util.Log
import com.example.sleep_cycle.data.SleepTimeDatabaseHelper
import com.example.sleep_cycle.data.model.SleepTime

class SleepTimeRepository(context: Context) {

    private val dbHelper = SleepTimeDatabaseHelper(context)

    fun addSleepTime(sleepTime: SleepTime, scheduleId: Long): Long {
        val db = dbHelper.writableDatabase
        val contentValues = ContentValues().apply {
            put("name", sleepTime.name)
            put("scheduleId", scheduleId)
            put("startTime", sleepTime.startTime)
            put("duration", sleepTime.duration)
        }
        val id = db.insert("SleepTimes", null, contentValues)
        db.close()
        return id
    }

    fun getSleepTimeById(id: Long): SleepTime? {
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM SleepTimes WHERE id = ?", arrayOf(id.toString()))

        return cursor.use { c ->
            if (c.moveToFirst()) {
                createSleepTimeFromCursor(c)
            } else {
                null
            }
        }.also {
            db.close()
        }
    }

    fun getAllSleepTimes(): List<SleepTime> {
        val sleepTimes = mutableListOf<SleepTime>()
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM SleepTimes", null)

        cursor.use { c ->
            while (c.moveToNext()) {
                sleepTimes.add(createSleepTimeFromCursor(c))
            }
        }

        db.close()
        return sleepTimes
    }

    fun updateSleepTime(sleepTime: SleepTime): Int {
        val db = dbHelper.writableDatabase

        Log.d("zaza123", sleepTime.toString())
        val contentValues = ContentValues().apply {
            put("name", sleepTime.name)
            put("scheduleId", sleepTime.scheduleId)
            put("startTime", sleepTime.startTime)
            put("duration", sleepTime.duration)
        }
        val result = db.update("SleepTimes", contentValues, "id = ?", arrayOf(sleepTime.id.toString()))
        db.close()
        return result
    }

    fun deleteSleepTime(id: Long): Int {
        val db = dbHelper.writableDatabase
        val result = db.delete("SleepTimes", "id = ?", arrayOf(id.toString()))
        db.close()
        return result
    }

    private fun createSleepTimeFromCursor(cursor: Cursor): SleepTime {
        return SleepTime(
            id = cursor.getLong(cursor.getColumnIndexOrThrow("id")),
            name = cursor.getString(cursor.getColumnIndexOrThrow("name")),
            scheduleId = cursor.getLong(cursor.getColumnIndexOrThrow("scheduleId")),
            startTime = cursor.getString(cursor.getColumnIndexOrThrow("startTime")),
            duration = cursor.getInt(cursor.getColumnIndexOrThrow("duration"))
        )
    }
}
