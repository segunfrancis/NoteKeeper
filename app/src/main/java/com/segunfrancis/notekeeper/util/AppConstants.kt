package com.segunfrancis.notekeeper.util

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.preferencesDataStore

object AppConstants {
    private const val DATA_STORE_NAME: String = "spotlight_pref"
    val SPOTLIGHT_PREF_KEY = booleanPreferencesKey(DATA_STORE_NAME)
    val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = DATA_STORE_NAME)
    const val WORKER_TAG: String = "BACKUP_WORKER_TAG"
}
