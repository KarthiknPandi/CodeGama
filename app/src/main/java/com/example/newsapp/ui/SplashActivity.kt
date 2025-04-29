package com.example.newsapp.ui

import android.content.Intent
import android.media.MediaPlayer
import android.media.MediaPlayer.OnCompletionListener
import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.OnApplyWindowInsetsListener
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.newsapp.R
import com.example.newsapp.databinding.ActivitySplashBinding
import com.example.newsapp.ui.auth.LoginActivity
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth

class SplashActivity : AppCompatActivity() {
    private var binding: ActivitySplashBinding? = null
    private var mAuth: FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.enableEdgeToEdge()
        binding = ActivitySplashBinding.inflate(getLayoutInflater())
        setContentView(binding!!.getRoot())
        ViewCompat.setOnApplyWindowInsetsListener(
            findViewById<View?>(R.id.main),
            OnApplyWindowInsetsListener { v: View?, insets: WindowInsetsCompat? ->
                val systemBars = insets!!.getInsets(WindowInsetsCompat.Type.systemBars())
                v!!.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
                insets
            })

        FirebaseApp.initializeApp(this)
        mAuth = FirebaseAuth.getInstance()

        playSplashVideo()
    }


    private fun playSplashVideo() {
        val videoUri =
            Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.splash_video)
        binding!!.videoView.setVideoURI(videoUri)
        binding!!.videoView.setOnCompletionListener(OnCompletionListener { mp: MediaPlayer? -> checkLoginStatus() })
        binding!!.videoView.setOnErrorListener(MediaPlayer.OnErrorListener { mp: MediaPlayer?, what: Int, extra: Int ->
            checkLoginStatus()
            true
        })
        binding!!.videoView.start()
    }

    private fun checkLoginStatus() {
        if (mAuth!!.getCurrentUser() != null) {
            startActivity(Intent(this@SplashActivity, MainActivity::class.java))
        } else {
            startActivity(Intent(this@SplashActivity, LoginActivity::class.java))
        }
        finish()
    }
}