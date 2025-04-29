package com.example.newsapp.ui.auth

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.OnApplyWindowInsetsListener
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.newsapp.R
import com.example.newsapp.databinding.ActivitySignUpBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest

class SignUpActivity : AppCompatActivity() {
    private var binding: ActivitySignUpBinding? = null
    private var mAuth: FirebaseAuth? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.enableEdgeToEdge()
        binding = ActivitySignUpBinding.inflate(getLayoutInflater())
        setContentView(binding!!.getRoot())
        ViewCompat.setOnApplyWindowInsetsListener(
            findViewById<View?>(R.id.main),
            OnApplyWindowInsetsListener { v: View?, insets: WindowInsetsCompat? ->
                val systemBars = insets!!.getInsets(WindowInsetsCompat.Type.systemBars())
                v!!.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
                insets
            })
        mAuth = FirebaseAuth.getInstance()

        binding!!.btnSignUp.setOnClickListener(View.OnClickListener { view: View? -> registerUser() })
        binding!!.tvAlreadyAccount.setOnClickListener(View.OnClickListener { view: View? ->
            startActivity(Intent(this@SignUpActivity, LoginActivity::class.java))
            finish()
        })
    }

    private fun registerUser() {
        val name = binding!!.etUserName.getText().toString().trim { it <= ' ' }
        val email = binding!!.etEmail.getText().toString().trim { it <= ' ' }
        val password = binding!!.etPassword.getText().toString().trim { it <= ' ' }
        val confirmPassword = binding!!.etConfirmPassword.getText().toString().trim { it <= ' ' }

        // Validate text fields
        if (TextUtils.isEmpty(name)) {
            binding!!.etUserName.setError("User name is required")
            binding!!.etUserName.requestFocus()
            return
        }

        if (TextUtils.isEmpty(email)) {
            binding!!.etEmail.setError("Email is required")
            binding!!.etEmail.requestFocus()
            return
        }

        if (TextUtils.isEmpty(password)) {
            binding!!.etPassword.setError("Password is required")
            binding!!.etPassword.requestFocus()
            return
        }

        if (password.length < 6) {
            binding!!.etPassword.setError("Password must be at least 6 characters")
            binding!!.etPassword.requestFocus()
            return
        }

        if (password != confirmPassword) {
            binding!!.etConfirmPassword.setError("Passwords do not match")
            binding!!.etConfirmPassword.requestFocus()
            return
        }

        // Firebase signup
        mAuth!!.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = FirebaseAuth.getInstance().currentUser
                    val profileUpdates = UserProfileChangeRequest.Builder()
                        .setDisplayName(name)
                        .build()

                    user?.updateProfile(profileUpdates)?.addOnCompleteListener { updateTask ->
                        if (updateTask.isSuccessful) {
                            Toast.makeText(
                                this@SignUpActivity,
                                "Registration successful! Please login.",
                                Toast.LENGTH_SHORT
                            ).show()
                            startActivity(Intent(this@SignUpActivity, LoginActivity::class.java))
                            finish()
                        } else {
                            Toast.makeText(
                                this@SignUpActivity,
                                "Failed to save display name.",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                } else {
                    Toast.makeText(
                        this@SignUpActivity,
                        "Registration failed: ${task.exception?.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }

    }
}