package com.example.newsapp.ui.auth

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.newsapp.R
import com.example.newsapp.databinding.ActivityLoginBinding
import com.example.newsapp.ui.MainActivity
import com.example.newsapp.utils.PrefManager
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {
    private var binding: ActivityLoginBinding? = null
    private var mAuth: FirebaseAuth? = null
    private var prefManager: PrefManager? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.enableEdgeToEdge()
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding!!.root)
        ViewCompat.setOnApplyWindowInsetsListener(
            findViewById(R.id.main)
        ) { v: View, insets: WindowInsetsCompat ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        mAuth = FirebaseAuth.getInstance()
        prefManager = PrefManager(this)

        binding!!.btnLogin.setOnClickListener { view: View? -> loginUser() }
        binding!!.tvSignUp.setOnClickListener { view: View? ->
            startActivity(
                Intent(
                    this@LoginActivity,
                    SignUpActivity::class.java
                )
            )
            finish()
        }
    }

    private fun loginUser() {
        val email = binding!!.etUsername.text.toString().trim { it <= ' ' }
        val password = binding!!.etPassword.text.toString().trim { it <= ' ' }

        // Validate inputs
        if (TextUtils.isEmpty(email)) {
            binding!!.etUsername.error = "Email is required"
            binding!!.etUsername.requestFocus()
            return
        }

        if (TextUtils.isEmpty(password)) {
            binding!!.etPassword.error = "Password is required"
            binding!!.etPassword.requestFocus()
            return
        }

        // Firebase Login
        mAuth!!.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(
                this
            ) { task: Task<AuthResult?> ->
                if (task.isSuccessful) {
                    val user = mAuth!!.currentUser
                    if (user != null) {
                        val userEmail = user.email
                        val userName =
                            if (user.displayName != null) user.displayName else "Unknown User"
                        val profilePic = if (user.photoUrl != null) user.photoUrl.toString() else ""

                        prefManager!!.saveUserDetails(userEmail, userName, profilePic)

                        Toast.makeText(
                            this@LoginActivity,
                            "Login Successful",
                            Toast.LENGTH_SHORT
                        ).show()
                        startActivity(
                            Intent(
                                this@LoginActivity,
                                MainActivity::class.java
                            )
                        )
                        finish()
                    }
                } else {
                    Toast.makeText(
                        this@LoginActivity,
                        "Login Failed: " + task.exception!!.message,
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
    }
}