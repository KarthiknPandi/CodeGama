package com.example.newsapp.utils

import android.content.Context
import android.content.SharedPreferences

class PrefManager(context: Context) {
    private val sharedPreferences: SharedPreferences
    private val editor: SharedPreferences.Editor

    init {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        editor = sharedPreferences.edit()
    }

    fun saveUserDetails(email: String?, name: String?, profilePicUrl: String?) {
        editor.putString(KEY_EMAIL, email)
        editor.putString(KEY_NAME, name)
        editor.putString(KEY_PROFILE_PIC, profilePicUrl)
        editor.apply()
    }

    val email: String
        get() = sharedPreferences.getString(KEY_EMAIL, "")!!

    val name: String
        get() = sharedPreferences.getString(KEY_NAME, "")!!

    val profilePic: String
        get() = sharedPreferences.getString(KEY_PROFILE_PIC, "")!!

    fun clearData() {
        editor.clear()
        editor.apply()
    }

    fun saveProfileImageUri(context: Context, uri: String) {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(KEY_PROFILE_IMAGE_URI, uri).apply()
    }

    fun getProfileImageUri(context: Context): String? {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return prefs.getString(KEY_PROFILE_IMAGE_URI, null)
    }


    companion object {
        private const val PREF_NAME = "newsAppPrefs"
        private const val KEY_EMAIL = "email"
        private const val KEY_NAME = "name"
        private const val KEY_PROFILE_PIC = "profile_pic"
        private const val KEY_PROFILE_IMAGE_URI = "profile_image_uri"
    }
}
