package com.example.expensetracker.data.local

import android.content.Context
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import androidx.core.content.edit

class SharedPreferencesManager(context: Context) {

    companion object {
        private const val PREFS_FILENAME = "expensetracker_secure_prefs"
        private const val KEY_AUTH_TOKEN = "auth_token"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_USERNAME = "username"
        private const val KEY_EMAIL = "email"
    }

    private val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)

    private val sharedPreferences = EncryptedSharedPreferences.create(
        PREFS_FILENAME,
        masterKeyAlias,
        context.applicationContext,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    fun saveAuthToken(token: String) {
        sharedPreferences.edit { putString(KEY_AUTH_TOKEN, token) }
    }

    fun getAuthToken(): String? {
        return sharedPreferences.getString(KEY_AUTH_TOKEN, null)
    }

    fun saveUserDetails(id: Long, username: String, email: String) {
        sharedPreferences.edit {
            putLong(KEY_USER_ID, id)
                .putString(KEY_USERNAME, username)
                .putString(KEY_EMAIL, email)
        }
    }

    fun getUserId(): Long? {
        val id = sharedPreferences.getLong(KEY_USER_ID, -1L)
        return if (id == -1L) null else id
    }

    fun getUsername(): String? {
        return sharedPreferences.getString(KEY_USERNAME, null)
    }

    fun getEmail(): String? {
        return sharedPreferences.getString(KEY_EMAIL, null)
    }

    fun clear() {
        sharedPreferences.edit { clear() }
    }
}