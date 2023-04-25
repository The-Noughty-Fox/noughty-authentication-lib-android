package com.noughtyfox.authentication

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class AuthStore(private val context: Context) {
    private companion object {
        val Context.dataStore: DataStore<Preferences> by preferencesDataStore("authType")
        val AUTH_TYPE_KEY = stringPreferencesKey("authType")
        const val GOOGLE_TYPE = "google"
        const val FACEBOOK_TYPE = "facebook"
    }

    fun getAuthType(): Flow<AuthType> = context.dataStore.data
        .map { preferences ->
            preferences[AUTH_TYPE_KEY] ?: ""
        }.map { type ->
            when (type) {
                GOOGLE_TYPE -> AuthType.Google
                FACEBOOK_TYPE -> AuthType.Facebook
                else -> AuthType.NONE
            }
        }

    suspend fun saveAuthType(authType: AuthType?) {
        context.dataStore.edit { preferences ->
            preferences[AUTH_TYPE_KEY] = authType?.name?.lowercase() ?: ""
        }
    }
}

enum class AuthType {
    Google, Facebook, NONE
}