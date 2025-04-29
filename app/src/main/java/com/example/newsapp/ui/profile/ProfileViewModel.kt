package com.example.newsapp.ui.profile


import android.app.Application
import android.content.Context
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class ProfileViewModel(application: Application) : AndroidViewModel(application) {

    private val _profileImageUri = MutableLiveData<Uri?>()
    val profileImageUri: LiveData<Uri?> = _profileImageUri

    private val sharedPref = application.getSharedPreferences("user_profile", Context.MODE_PRIVATE)

    init {
        val uriString = sharedPref.getString("profile_image_uri", null)
        if (uriString != null) {
            _profileImageUri.value = Uri.parse(uriString)
        }
    }

    fun updateProfileImage(uri: Uri) {
        _profileImageUri.value = uri
        sharedPref.edit().putString("profile_image_uri", uri.toString()).apply()
    }
}
