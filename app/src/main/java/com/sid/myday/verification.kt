package com.sid.myday


import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase


class verification : AppCompatActivity() {


    private lateinit var auth: FirebaseAuth
    private lateinit var cBar: ProgressBar


    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_verification)

        cBar = findViewById(R.id.cBar)
        val contBtn: Button = findViewById(R.id.cont)
        auth = Firebase.auth

        val bundle = intent.extras
        if (bundle != null) {
            val usrEmail1: String = bundle.getString("email")!!
            val usrPass1: String = bundle.getString("pass")!!

            contBtn.setOnClickListener {
                cBar.visibility = View.VISIBLE
                auth.signInWithEmailAndPassword(usrEmail1, usrPass1)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            // Sign in success, update UI with the signed-in user's information
                            val verification = auth.currentUser?.isEmailVerified
                            if (verification == true) {
                                updateUIlogin()
                                finish()

                            } else {
                                Toast.makeText(this, "Please verify your email", Toast.LENGTH_SHORT)
                                    .show()
                                cBar.visibility = View.INVISIBLE

                            }
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(
                                baseContext, "Authentication failed.",
                                Toast.LENGTH_SHORT
                            ).show()
                            cBar.visibility = View.INVISIBLE
                        }
                    }
            }
        }
    }

    private fun updateUIlogin() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent);
        cBar.visibility = View.INVISIBLE
    }
}