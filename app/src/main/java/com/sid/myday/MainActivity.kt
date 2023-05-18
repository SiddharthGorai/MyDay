package com.sid.myday

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ArrayAdapter
import android.widget.ImageButton
import android.widget.ListView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import java.util.Calendar


class MainActivity : AppCompatActivity() {
    private lateinit var mDatabase: DatabaseReference
    private val userId = FirebaseAuth.getInstance().currentUser!!.uid
    private lateinit var section: String
    private lateinit var signoutAlert: AlertDialog.Builder
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var calendar: Calendar
    private lateinit var actionBarDrawerToggle: ActionBarDrawerToggle
    private lateinit var pBar: ProgressBar
    private lateinit var subj: Array<String>

    private fun changePassword(menu: Menu) {

        val auth = Firebase.auth
        val dEmail = menu.findItem(R.id.demail)

        val uEmail = dEmail.title.toString()


        auth.sendPasswordResetEmail(uEmail.trim())
            .addOnSuccessListener {
                val alertDialog = AlertDialog.Builder(this)
                alertDialog.setTitle("MyDay")
                    .setCancelable(true)
                    .setMessage("A password change link has been sent to your eamil.")
                    .setNegativeButton("Ok") { dialogInterface, it ->
                        dialogInterface.cancel()
                    }.show()
            }
            .addOnFailureListener {
                Toast.makeText(this, it.toString(), Toast.LENGTH_SHORT).show()
            }


    }

    private fun changeMenuBar(menu: Menu?) {

        mDatabase = Firebase.database.reference
        mDatabase.child("User").child(userId).get().addOnSuccessListener {
            val uName = it.child("usrName").value.toString()
            val uEmail = it.child("usrEmail").value.toString()
            val uSec = it.child("section").value.toString()

            val dName = menu?.findItem(R.id.dname)
            val dEmail = menu?.findItem(R.id.demail)
            val dSec = menu?.findItem(R.id.section)

            dEmail?.title = uEmail
            dName?.title = uName
            dSec?.title = "Section: " + uSec


        }.addOnFailureListener {
            Toast.makeText(this, it.toString(), Toast.LENGTH_SHORT).show()
        }

    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        return if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            true
        } else {
            return super.onOptionsItemSelected(item)
        }
    }

    @SuppressLint("MissingInflatedId", "ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        getSupportActionBar()?.setElevation(0F)
        getSupportActionBar()?.setBackgroundDrawable(ColorDrawable(getColor(R.color.pink)))
        getSupportActionBar()?.setDisplayHomeAsUpEnabled(true)

        drawerLayout = findViewById(R.id.my_drawer_layout)
        actionBarDrawerToggle = ActionBarDrawerToggle(
            this, drawerLayout,
            R.string.nav_open, R.string.nav_close
        )

        drawerLayout.addDrawerListener(actionBarDrawerToggle)
        actionBarDrawerToggle.syncState()

        pBar = findViewById(R.id.pBar)
        pBar.visibility = View.VISIBLE

        FirebaseMessaging.getInstance().subscribeToTopic("news")
            .addOnCompleteListener { task ->
                var msg = "Done"
                if (!task.isSuccessful) {
                    msg = "Failed"
                }
            }


        val navView: NavigationView = findViewById(R.id.navView)
        val menu: Menu = navView.menu
        changeMenuBar(menu)
        navView.setNavigationItemSelectedListener { it ->
            when (it.itemId) {
                R.id.signOut -> doThis()
                R.id.changePass -> {

                    changePassword(menu)}

            }
            true
        }

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(android.Manifest.permission.POST_NOTIFICATIONS), 101
                )
            }
            if (ContextCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.SCHEDULE_EXACT_ALARM
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(android.Manifest.permission.SCHEDULE_EXACT_ALARM), 102
                )
            }
        }


        val calender: Calendar = Calendar.getInstance()
        var day: Int = calender.get(Calendar.DAY_OF_WEEK)
        day -= 1
        var subjectNot = arrayOf<String>()


        val arrowLeft: ImageButton = findViewById(R.id.arrowL)
        val arrowRight: ImageButton = findViewById(R.id.arrowR)
        mDatabase = Firebase.database.reference
        mDatabase.child("User").child(userId).get().addOnSuccessListener { it ->
            for (i in it.children) {
                section = i.value.toString()
                showData(day)
                mDatabase.child(section).child(day.toString()).get().addOnSuccessListener { it ->
                    for (i in it.children) {
                        subjectNot += i.value.toString()
                        if(subjectNot.size == 4) subjectNot += " "

                    }
//                    setAlarm(7, 50, subjectNot)
                }.addOnFailureListener { it ->
                }

                break
            }

        }.addOnFailureListener { it ->

        }


        arrowLeft.setOnClickListener {

            if (day == 0) {
                day = 6
                showData(day)
            } else {
                day -= 1
                showData(day)
            }
        }
        arrowRight.setOnClickListener {
            if (day == 6) {
                day = 0
                showData(day)
            } else {
                day += 1
                showData(day)
            }
        }


    }

    private fun showData(day: Int) {
        val tDay: TextView = findViewById(R.id.timeText)
        when (day) {
            1 -> tDay.text = "Monday"
            2 -> tDay.text = "Tuesday"
            3 -> tDay.text = "Wednesday"
            4 -> tDay.text = "Thursday"
            5 -> tDay.text = "Friday"
            6 -> tDay.text = "Saturday"
            0 -> tDay.text = "Sunday"
        }

// sun - 1 sat - 7
        val arrayAdapterTime: ArrayAdapter<*>
        var arrayAdapterSubj: ArrayAdapter<*>
        val time = arrayOf(
            "9:00-9:50", "10:00-10:50", "11:00-11:50",
            "12:00-12:50", "2:00-2:50", "3:00-3:50", "4:00-4:50", "5:00-5:50", "6:00-6:50"
        )
        var mListTime = findViewById<ListView>(R.id.userListTime)
        var mListSubj = findViewById<ListView>(R.id.userList)

        arrayAdapterTime = ArrayAdapter(
            this,
            android.R.layout.simple_list_item_1, time
        )
        mListTime.adapter = arrayAdapterTime


        subj = arrayOf()

        mDatabase.child(section).child(day.toString()).get().addOnSuccessListener { it ->
            for (i in it.children) {
                subj += i.value.toString()
            }
            arrayAdapterSubj = ArrayAdapter(this, android.R.layout.simple_list_item_1, subj)
            mListSubj.adapter = arrayAdapterSubj
            pBar.visibility = View.INVISIBLE

        }.addOnFailureListener { it ->

        }




    }

    private fun doThis() {
        signoutAlert = AlertDialog.Builder(this)
        signoutAlert.setTitle("Alert!")
            .setMessage("Do you want to Sign out ?")
            .setCancelable(true)
            .setPositiveButton("Yes") { dialogInterface, it ->
                Firebase.auth.signOut()
                val intent = Intent(this, loginActivity::class.java)
                startActivity(intent)
                finish()
            }
            .setNegativeButton("No") { dialogInterface, it ->
                dialogInterface.cancel()
            }.show()

    }

    private fun setAlarm(hour: Int, min: Int, subject: Array<String>) {
        calendar = Calendar.getInstance()
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)

        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager


        var hourvar = hour
        for (i in 0..9) {
            if (subject[i].trim().isNotEmpty()) {
                val intent = Intent(this, MyReciever::class.java)
                intent.putExtra("data", subject[i])

                calendar.set(Calendar.HOUR_OF_DAY, hourvar)
                calendar.set(Calendar.MINUTE, min)

                val pendingIntent = PendingIntent.getBroadcast(
                    this,
                    i, intent, PendingIntent.FLAG_IMMUTABLE
                )
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)

            }
            hourvar += 1
        }

        Toast.makeText(this, "Alarm set successfully..", Toast.LENGTH_SHORT).show()

    }

}