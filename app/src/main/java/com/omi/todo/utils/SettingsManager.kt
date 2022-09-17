package com.omi.todo.utils

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

enum class UiMode {
    LIGHT, DARK
}


private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings_pref")

class SettingsManager(context: Context) {
    private val mDataStore: DataStore<Preferences> = context.dataStore

    val uiModeFlow: Flow<UiMode> = context.dataStore.data
        .catch {
            if (it is IOException) {
                it.printStackTrace()
                emit(emptyPreferences())
            } else {
                throw it
            }
        }
        .map { preference ->
            val isDarkMode = preference[IS_DARK_MODE] ?: false

            when (isDarkMode) {
                true -> UiMode.DARK
                false -> UiMode.LIGHT
            }
        }

    suspend fun setUiMode(uiMode: UiMode) {
        mDataStore.edit { preferences ->
            preferences[IS_DARK_MODE] = when (uiMode) {
                UiMode.LIGHT -> false
                UiMode.DARK -> true
            }
        }
    }

    companion object {
        val IS_DARK_MODE = booleanPreferencesKey("dark_mode")
    }
}