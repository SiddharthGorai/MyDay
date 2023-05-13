package com.sid.myday

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class forgetPassword : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forget_password)

        getSupportActionBar()?.setDisplayHomeAsUpEnabled(true)

        val etEmail = findViewById<EditText>(R.id.forEmail)
        val forBtn = findViewById<Button>(R.id.forBtn)
        val fBar = findViewById<ProgressBar>(R.id.fBar)
        auth = Firebase.auth

        forBtn.setOnClickListener {
                fBar.visibility = View.VISIBLE
                val uEmail = etEmail.text.toString()

                if (uEmail.trim().isEmpty()) {
                    etEmail.setError("Please enter Email")
                    etEmail.requestFocus()
                } else {

                        auth.sendPasswordResetEmail(uEmail.trim())
                            .addOnSuccessListener {
                                fBar.visibility = View.INVISIBLE
                                Toast.makeText(
                                    this,
                                    "Please check your mail to reset password",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                            .addOnFailureListener {
                                Toast.makeText(this, it.toString(), Toast.LENGTH_SHORT).show()
                            }
                    }

                    }

            }

}


