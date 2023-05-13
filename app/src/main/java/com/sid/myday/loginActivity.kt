package com.sid.myday

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class loginActivity : AppCompatActivity() {
    private lateinit var logEmail: EditText
    private lateinit var logPass: EditText
    private lateinit var inputEmail: EditText
    private lateinit var inputPass: EditText
    private lateinit var inputConPass: EditText
    private lateinit var inputUsername: EditText
    private lateinit var pBar: ProgressBar
    private lateinit var mBar: ProgressBar
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference

    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)



        val signUpTxt: TextView = findViewById(R.id.signUp)
        val logInTxt: TextView =  findViewById(R.id.logIN)
        val signInbtn: Button = findViewById(R.id.regButton)
        val logInbtn: Button = findViewById(R.id.lButton)
        val signInLayout : LinearLayout = findViewById(R.id.signInLayout)
        val logInLayout: LinearLayout = findViewById(R.id.logInLayout)
        val forPass: TextView = findViewById(R.id.forgetPass)

        val spinner: Spinner = findViewById(R.id.spinner)
        if (spinner != null){
            val listSec = arrayOf("Select section:","A","B","C","D")

            val spinnerAdapter= object : ArrayAdapter<String>
                (this,R.layout.spinner_item, listSec) {

                override fun isEnabled(position: Int): Boolean {
                    // Disable the first item from Spinner
                    // First item will be used for hint
                    return position != 0
                }
                override fun getDropDownView(
                    position: Int,
                    convertView: View?,
                    parent: ViewGroup
                ): View {
                    val view: TextView = super.getDropDownView(position, convertView, parent) as TextView
                    //set the color of first item in the drop down list to gray
                    if(position == 0) {
                        view.setTextColor(Color.GRAY)
                    } else {
                        //here it is possible to define color for other items by
                        //view.setTextColor(Color.RED)
                    }
                    return view
                }

            }
            spinner.adapter = spinnerAdapter

        }

        signUpTxt.setOnClickListener {
            signUpTxt.background = resources.getDrawable(R.drawable.switch_trcks,null)
            signUpTxt.setTextColor(resources.getColor(R.color.txtColor,null))
            logInTxt.background = null
            signInLayout.visibility = View.VISIBLE
            logInLayout.visibility = View.GONE
            logInbtn.visibility = View.GONE
            signInbtn.visibility = View.VISIBLE
            logInTxt.setTextColor(resources.getColor(R.color.pink,null))

        }
        logInTxt.setOnClickListener {
            logInTxt.background = resources.getDrawable(R.drawable.switch_trcks,null)
            logInTxt.setTextColor(resources.getColor(R.color.txtColor,null))
            signUpTxt.background = null
            logInLayout.visibility = View.VISIBLE
            signInbtn.visibility = View.GONE
            logInbtn.visibility = View.VISIBLE
            signInLayout.visibility = View.GONE
            signUpTxt.setTextColor(resources.getColor(R.color.pink,null))

        }

        logEmail = findViewById(R.id.lEmail)
        logPass = findViewById(R.id.lPass)
//        btnLog = findViewById(R.id.lButton)
        auth = Firebase.auth



        logInbtn.setOnClickListener {
            login(logEmail, logPass)
        }
        signInbtn.setOnClickListener{
            signin()
        }
        forPass.setOnClickListener {
            forgetPassword()
        }



    }

    private fun login(logEmail: EditText, logPass: EditText) {

        pBar = findViewById(R.id.pBar)

        val llEmail: String = logEmail.text.toString().trim()
        val llPass: String = logPass.text.toString().trim()

        if (llEmail.isEmpty()) {
            logEmail.error = "Please enter email."
            logEmail.requestFocus()
        }
        else if (llPass.isEmpty()){
            logPass.error = "Please enter password."
            logPass.requestFocus()
        }
        else {
            pBar.visibility = View.VISIBLE
            auth.signInWithEmailAndPassword(llEmail, llPass)
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
                            pBar.visibility = View.INVISIBLE

                        }
                    } else {
                        // If sign in fails, display a message to the user.
                        Toast.makeText(
                            baseContext, "Authentication failed.",
                            Toast.LENGTH_SHORT
                        ).show()
                        pBar.visibility = View.INVISIBLE
                    }
                }
        }
    }

    private fun updateUIlogin() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent);
        pBar.visibility = View.INVISIBLE
    }

    private fun signin() {
        inputEmail = findViewById(R.id.usrEmail)
        inputPass = findViewById(R.id.usrPass)
        inputConPass = findViewById(R.id.usrConPass)
        inputUsername = findViewById(R.id.usrName)

        mBar = findViewById(R.id.pBar)

        val strEmail: String = inputEmail.text.toString().trim()
        val strPass: String = inputPass.text.toString().trim()
        val strConPass: String = inputConPass.text.toString().trim()
        val strUsrname: String = inputUsername.text.toString().trim()

        val spinner: Spinner = findViewById(R.id.spinner)
        val sText: String = spinner.getSelectedItem().toString()


        if (strEmail.isEmpty()) {
            inputEmail.error = "Please enter correct email."
            inputEmail.requestFocus()
        }
        else if (strPass.isEmpty() || strPass.length < 6) {
            inputPass.error = "Please enter password greater than 6 characters."
            inputPass.requestFocus()
        }
        else if (strUsrname.isEmpty()) {
            inputUsername.error = "Please enter username."
            inputUsername.requestFocus()
        } else if (strConPass != strPass) {
            inputConPass.error = "Password not matching."
            inputConPass.requestFocus()
        }else if(sText == "Select section:"){
            Toast.makeText(this,"Please select section",Toast.LENGTH_SHORT).show()
        }
        else {
            mBar.visibility = View.VISIBLE
            auth = Firebase.auth
            database = Firebase.database.reference

            auth.createUserWithEmailAndPassword(strEmail, strPass)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        auth.currentUser?.sendEmailVerification()
                            ?.addOnSuccessListener {
                                Toast.makeText(this, "verification sent", Toast.LENGTH_SHORT).show()
                                saveData(strUsrname, strEmail,sText)
                                updateUIsignin(strEmail,strPass)
                            }


                    } else {
                        // If sign in fails, display a message to the user.
                        Toast.makeText(
                            baseContext, "Authentication failed.",
                            Toast.LENGTH_SHORT
                        ).show()
                        mBar.visibility = View.INVISIBLE
                    }
                }
        }


    }

    private fun saveData(strUsrname: String, strEmail: String, sText: String) {

        val user = userData(strUsrname,strEmail,sText,null,null)
        val userID = FirebaseAuth.getInstance().currentUser!!.uid
        database.child("User").child(userID).setValue(user)

    }

    private fun updateUIsignin(etEmail:String, etPass:String) {
        Firebase.auth.signOut()

        val bundle = Bundle()
        bundle.putString("email", etEmail)
        bundle.putString("pass", etPass)

        val intent = Intent(this, verification::class.java)
        intent.putExtras(bundle)
        startActivity(intent)

        mBar.visibility = View.INVISIBLE
        inputEmail.setText("")
        inputPass.setText("")
        inputConPass.setText("")
        inputUsername.setText("")

    }

    private fun forgetPassword() {
        val intent = Intent(this,forgetPassword::class.java)
        startActivity(intent)
    }

}