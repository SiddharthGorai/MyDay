package com.sid.myday

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import java.util.*


class MyReciever : BroadcastReceiver() {
    private lateinit var calendar: Calendar
    override fun onReceive(context: Context, intent: Intent) {

        val sub: String? = intent.getStringExtra("data")

        val channelID = "CHANNEL_ID_NOTIFICATION"
        val builder = NotificationCompat.Builder(context, channelID)
        builder.setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("MyDay")
            .setContentText(sub)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)


        val notificationManagerCompat: NotificationManagerCompat =
            NotificationManagerCompat.from(context)

        val intent = Intent(context, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent: PendingIntent = PendingIntent.getActivity(
            context,
            0, intent, PendingIntent.FLAG_MUTABLE
        )

        builder.setContentIntent(pendingIntent)
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {

            val importance = NotificationManager.IMPORTANCE_HIGH
            val notificationChannel = NotificationChannel(
                channelID, " Some description",
                importance
            )
            notificationChannel.lightColor = Color.GREEN
            notificationChannel.enableVibration(true)
            notificationManagerCompat.createNotificationChannel(notificationChannel)


        }
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    context,
                    android.Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    MainActivity(),
                    arrayOf(android.Manifest.permission.POST_NOTIFICATIONS), 101
                )
            }
        }
        notificationManagerCompat.notify(0, builder.build())


    }
}

