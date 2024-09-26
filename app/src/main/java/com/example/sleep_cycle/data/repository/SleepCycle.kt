package com.example.sleep_cycle.data.repository

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import android.widget.Toast
import com.example.sleep_cycle.data.SleepTimeDatabaseHelper
import com.example.sleep_cycle.data.models.SleepCycle
import com.example.sleep_cycle.data.model.SleepTime
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class SleepCycleRepository@Inject constructor(
    @ApplicationContext private val context: Context
){

    private val dbHelper = SleepTimeDatabaseHelper(context)

    fun addSleepCycle(sleepCycle: SleepCycle): Long {
        val db = dbHelper.writableDatabase
        return try {
            val contentValues = ContentValues().apply {
                put("name", sleepCycle.name)
            }
            val cycleId = db.insert("SleepCycles", null, contentValues)

            if (cycleId != -1L) {
                sleepCycle.sleepTimes.forEach { sleepTime ->
                    val sleepTimeValues = ContentValues().apply {
                        put("name", sleepTime.name)
                        put("scheduleId", cycleId)
                        put("startTime", sleepTime.startTime)
                        put("duration", sleepTime.duration)
                    }
                    db.insert("SleepTimes", null, sleepTimeValues)
                }
            }

            cycleId
        } catch (e: Exception) {
            Log.e("SleepCycleRepository", "Error saving SleepCycle: ${e.message}", e)

            Toast.makeText(context, "Error saving SleepCycle: ${e.message}", Toast.LENGTH_SHORT).show()

            -1L
        } finally {
            db.close()
        }
    }

    fun getSleepCycleById(id: Long): SleepCycle? {
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM SleepCycles WHERE id = ?", arrayOf(id.toString()))

        return if (cursor.moveToFirst()) {
            val name = cursor.getString(cursor.getColumnIndexOrThrow("name"))
//            val isActive = cursor.getInt(cursor.getColumnIndexOrThrow("isActive"))

            val sleepTimes = mutableListOf<SleepTime>()
            val sleepTimeCursor = db.rawQuery("SELECT * FROM SleepTimes WHERE scheduleId = ?", arrayOf(id.toString()))
            if (sleepTimeCursor.moveToFirst()) {
                do {
                    val sleepTime = SleepTime(
                        id = sleepTimeCursor.getLong(sleepTimeCursor.getColumnIndexOrThrow("id")),
                        name = sleepTimeCursor.getString(sleepTimeCursor.getColumnIndexOrThrow("name")),
                        scheduleId = sleepTimeCursor.getLong(sleepTimeCursor.getColumnIndexOrThrow("scheduleId")),
                        startTime = sleepTimeCursor.getString(sleepTimeCursor.getColumnIndexOrThrow("startTime")),
                        duration = sleepTimeCursor.getInt(sleepTimeCursor.getColumnIndexOrThrow("duration"))
                    )
                    sleepTimes.add(sleepTime)
                } while (sleepTimeCursor.moveToNext())
            }
            sleepTimeCursor.close()

            SleepCycle(id, name, sleepTimes, 0 )
        } else {
            null
        }.also {
            cursor.close()
            db.close()
        }
    }

    fun getAllSleepCycles(): List<SleepCycle> {
        val sleepCycles = mutableListOf<SleepCycle>()
        val db = dbHelper.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM SleepCycles", null)

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getLong(cursor.getColumnIndexOrThrow("id"))
                val name = cursor.getString(cursor.getColumnIndexOrThrow("name"))
                val isActive = cursor.getInt(cursor.getColumnIndexOrThrow("isActive"))

                val sleepTimes = mutableListOf<SleepTime>()
                val sleepTimeCursor = db.rawQuery("SELECT * FROM SleepTimes WHERE scheduleId = ?", arrayOf(id.toString()))
                if (sleepTimeCursor.moveToFirst()) {
                    do {
                        val sleepTime = SleepTime(
                            id = sleepTimeCursor.getLong(sleepTimeCursor.getColumnIndexOrThrow("id")),
                            name = sleepTimeCursor.getString(sleepTimeCursor.getColumnIndexOrThrow("name")),
                            scheduleId = sleepTimeCursor.getLong(sleepTimeCursor.getColumnIndexOrThrow("scheduleId")),
                            startTime = sleepTimeCursor.getString(sleepTimeCursor.getColumnIndexOrThrow("startTime")),
                            duration = sleepTimeCursor.getInt(sleepTimeCursor.getColumnIndexOrThrow("duration"))
                        )
                        sleepTimes.add(sleepTime)
                    } while (sleepTimeCursor.moveToNext())
                }
                sleepTimeCursor.close()

                sleepCycles.add(SleepCycle(id, name, sleepTimes, isActive))
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()
        return sleepCycles
    }

    fun toggleActive(id: Long): Boolean {
        val db = dbHelper.writableDatabase

        return try {
            db.beginTransaction()

            // Query to find the currently active entry
            val currentActiveCursor = db.rawQuery("SELECT id FROM SleepCycles WHERE isActive = 1", null)
            var activeId: Long? = null

            // Check if the cursor has any results and move to the first row
            if (currentActiveCursor.moveToFirst()) {
                activeId = currentActiveCursor.getLong(currentActiveCursor.getColumnIndexOrThrow("id"))

                if (activeId != id) {
                    db.execSQL("UPDATE SleepCycles SET isActive = 0 WHERE id = ?", arrayOf(activeId))
                }
            }
            currentActiveCursor.close()

            if (activeId == id) {
                db.execSQL("UPDATE SleepCycles SET isActive = 0 WHERE id = ?", arrayOf(id))
            } else {
                db.execSQL("UPDATE SleepCycles SET isActive = 1 WHERE id = ?", arrayOf(id))
            }

            db.setTransactionSuccessful()
            true
        } catch (e: Exception) {
            Log.e("DatabaseError", "Error toggling active sleep cycle: ${e.message}")
            false
        } finally {
            db.endTransaction()
            db.close()
        }
    }


    fun getActiveSleepCycle() : SleepCycle? {
        val db = dbHelper.readableDatabase

        val cursor = db.rawQuery("SELECT * FROM SleepCycles WHERE isActive = 1", null)

        return if (cursor.moveToFirst()) {
            val name = cursor.getString(cursor.getColumnIndexOrThrow("name"))
//            val isActive = cursor.getInt(cursor.getColumnIndexOrThrow("isActive"))
            val id = cursor.getLong(cursor.getColumnIndexOrThrow("id"))


            val sleepTimes = mutableListOf<SleepTime>()
            val sleepTimeCursor = db.rawQuery("SELECT * FROM SleepTimes WHERE scheduleId = ?", arrayOf(id.toString()))
            if (sleepTimeCursor.moveToFirst()) {
                do {
                    val sleepTime = SleepTime(
                        id = sleepTimeCursor.getLong(sleepTimeCursor.getColumnIndexOrThrow("id")),
                        name = sleepTimeCursor.getString(sleepTimeCursor.getColumnIndexOrThrow("name")),
                        scheduleId = sleepTimeCursor.getLong(sleepTimeCursor.getColumnIndexOrThrow("scheduleId")),
                        startTime = sleepTimeCursor.getString(sleepTimeCursor.getColumnIndexOrThrow("startTime")),
                        duration = sleepTimeCursor.getInt(sleepTimeCursor.getColumnIndexOrThrow("duration"))
                    )
                    sleepTimes.add(sleepTime)
                } while (sleepTimeCursor.moveToNext())
            }
            sleepTimeCursor.close()

            SleepCycle(id, name, sleepTimes, 0 )
        } else {
            null
        }.also {
            cursor.close()
            db.close()
        }
    }


    fun saveSleepTimes(cycleId: Long, sleepTimes: List<SleepTime>): Boolean {
        val db = dbHelper.writableDatabase
        return try {
            db.beginTransaction()

            db.delete("SleepTimes", "scheduleId = ?", arrayOf(cycleId.toString()))

            sleepTimes.forEach { sleepTime ->
                val sleepTimeValues = ContentValues().apply {
                    put("name", sleepTime.name)
                    put("scheduleId", cycleId)
                    put("startTime", sleepTime.startTime)
                    put("duration", sleepTime.duration)
                }
                db.insert("SleepTimes", null, sleepTimeValues)
            }

            db.setTransactionSuccessful()
            true
        } catch (e: Exception) {
            Log.e("SleepCycleRepository", "Error saving SleepTimes: ${e.message}", e)
            false
        } finally {
            db.endTransaction()
            db.close()
        }
    }

    fun updateSleepCycle(sleepCycle: SleepCycle): Int {
        val db = dbHelper.writableDatabase
        val contentValues = ContentValues().apply {
            put("name", sleepCycle.name)
        }
        val result = db.update("SleepCycles", contentValues, "id = ?", arrayOf(sleepCycle.id.toString()))

        db.delete("SleepTimes", "scheduleId = ?", arrayOf(sleepCycle.id.toString()))  // Simplified: delete old SleepTimes
        sleepCycle.sleepTimes.forEach { sleepTime ->
            val sleepTimeValues = ContentValues().apply {
                put("name", sleepTime.name)
                put("scheduleId", sleepCycle.id)
                put("startTime", sleepTime.startTime)
                put("duration", sleepTime.duration)
            }
            db.insert("SleepTimes", null, sleepTimeValues)
        }

        db.close()
        return result
    }

    fun deleteSleepCycle(id: Long): Boolean {
        val db = dbHelper.writableDatabase

        return try{
            db.delete("SleepTimes", "scheduleId = ?", arrayOf(id.toString()))  // Delete associated SleepTimes
            db.delete("SleepCycles", "id = ?", arrayOf(id.toString()))
            true
        }

        catch (e: Exception){
            Log.e("Database error", e.message.toString())
            false
        }

        finally {
            db.close()
        }
    }
}
