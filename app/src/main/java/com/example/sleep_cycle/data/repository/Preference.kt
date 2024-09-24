package com.example.sleep_cycle.data.repository

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class Preference@Inject constructor(@ApplicationContext private val context: Context) {
    private val Context.dataStore by preferencesDataStore(name = "user_preferences")

    companion object {
        val MODE_KEY = booleanPreferencesKey("mode")
    }

    suspend fun saveMode(isClockMode: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[MODE_KEY] = isClockMode
        }
    }

    public val modeFlow: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[MODE_KEY] ?: true // default value if not found
    }
}
