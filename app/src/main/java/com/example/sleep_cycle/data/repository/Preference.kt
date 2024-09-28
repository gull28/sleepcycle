package com.example.sleep_cycle.data.repository

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class Preference @Inject constructor(@ApplicationContext private val context: Context) {
    private val Context.dataStore by preferencesDataStore(name = "user_preferences")

    companion object {
        val MODE_KEY = booleanPreferencesKey("mode")
        val NOTIFICATION_PERMISSION_KEY = booleanPreferencesKey("notification_permission")
        val BATTERY_INFO_SHOWN_KEY = booleanPreferencesKey("battery_info_shown")
        val DATABASE_SEEDED_KEY = booleanPreferencesKey("database_seeded")
    }

    suspend fun saveMode(isClockMode: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[MODE_KEY] = isClockMode
        }
    }

    val modeFlow: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[MODE_KEY] ?: true
    }


    val batteryInfoShownFlow: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[BATTERY_INFO_SHOWN_KEY] ?: false
    }

    suspend fun saveNotificationPermission(isGranted: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[NOTIFICATION_PERMISSION_KEY] = isGranted
        }
    }

    suspend fun saveBatteryInfoShownFlow(shown: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[BATTERY_INFO_SHOWN_KEY] = shown
        }
    }

    val notificationPermissionFlow: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[NOTIFICATION_PERMISSION_KEY] ?: false
    }

    suspend fun setDatabaseSeeded() {
        context.dataStore.edit { preferences ->
            preferences[DATABASE_SEEDED_KEY] = true
        }
    }

    val isDatabaseSeededFlow: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[DATABASE_SEEDED_KEY] ?: false
    }
}
