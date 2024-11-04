package com.example.sleep_cycle.data.repository

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

private val Context.dataStore by preferencesDataStore(name = "user_preferences")

class Preference @Inject constructor(@ApplicationContext private val context: Context) {

    companion object {
        val MODE_KEY = booleanPreferencesKey("mode")
        val NOTIFICATION_PERMISSION_KEY = booleanPreferencesKey("notification_permission")
        val BATTERY_INFO_SHOWN_KEY = booleanPreferencesKey("battery_info_shown")
        val DATABASE_SEEDED_KEY = booleanPreferencesKey("database_seeded")
    }

    // Saves the "mode" preference
    suspend fun saveMode(isClockMode: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[MODE_KEY] = isClockMode
        }
    }

    // Flow to observe "mode" preference changes
    val modeFlow: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[MODE_KEY] ?: true
    }

    // Flow to observe "battery info shown" preference changes
    val batteryInfoShownFlow: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[BATTERY_INFO_SHOWN_KEY] ?: false
    }

    // Saves the "notification permission" preference
    suspend fun saveNotificationPermission(isGranted: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[NOTIFICATION_PERMISSION_KEY] = isGranted
        }
    }

    // Saves the "battery info shown" preference
    suspend fun saveBatteryInfoShownFlow(shown: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[BATTERY_INFO_SHOWN_KEY] = shown
        }
    }

    // Flow to observe "notification permission" preference changes
    val notificationPermissionFlow: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[NOTIFICATION_PERMISSION_KEY] ?: false
    }

    // Sets the "database seeded" preference to true
    suspend fun setDatabaseSeeded() {
        context.dataStore.edit { preferences ->
            preferences[DATABASE_SEEDED_KEY] = true
        }
    }

    // Flow to observe "database seeded" preference changes
    val isDatabaseSeededFlow: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[DATABASE_SEEDED_KEY] ?: false
    }
}
