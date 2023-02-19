package com.udacity

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.udacity.ui.screens.details.DetailActivity

const val CHANNEL_ID = "channelId"
private val NOTIFICATION_ID = 0
var selectedDownloadUri: URL = URL.DEFAULT
var downloadStatus = "Fail"

// Create notification channel
fun createNotificationChannel(context: Context) {
    // Check sdk version
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        // Initialize channel
        // Pass channel id and name
        // Set Importance to high
        val notificationChannel = NotificationChannel(
            CHANNEL_ID,
            "LOAD_APP_CHANNEL",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            // Disable notification badge
            setShowBadge(false)
        }
        // Set description
        notificationChannel.description = "Download complete!"

        // Initialize notification manager then create the notification channel
        val notificationManager = context.getSystemService(NotificationManager::class.java)
        notificationManager.createNotificationChannel(notificationChannel)
    }
}


fun createNotification(context: Context) {

    // Initialize notification manager
    val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    // initialize intent with detail activity as parameter
    val detailIntent = Intent(context, DetailActivity::class.java)

    // create extra value to pass file name
    detailIntent.putExtra("fileName", selectedDownloadUri.title)

    // create extra value to pass status
    detailIntent.putExtra("status", downloadStatus)

    // Check build version to assign pending flag
    val pendingFlag = if (Build.VERSION.SDK_INT < 31) {
        PendingIntent.FLAG_UPDATE_CURRENT
    } else {
        PendingIntent.FLAG_MUTABLE
    }


    // Create and initialize pending intent
    val pendingIntent = TaskStackBuilder.create(context).run {
        addNextIntentWithParentStack(detailIntent)
        getPendingIntent(0, pendingFlag)
    } as PendingIntent

    // Create notification action that will run pending intent after clicked
    val action = NotificationCompat.Action(
        R.drawable.ic_assistant_black_24dp,
        "See Changes",
        pendingIntent
    )


    val contentIntent = Intent(DownloadManager.ACTION_VIEW_DOWNLOADS).apply {
        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
    }

    val contentPendingIntent = PendingIntent.getActivity(
        context,
        NOTIFICATION_ID,
        contentIntent,
        pendingFlag
    )

    // Notification builder
    val builder = NotificationCompat.Builder(context, CHANNEL_ID)
        .setSmallIcon(R.drawable.ic_assistant_black_24dp)
        .setContentTitle(selectedDownloadUri.title)
        .setContentText(selectedDownloadUri.text)
        .setContentIntent(contentPendingIntent)
        .addAction(action)
        .setAutoCancel(true)
        .setPriority(NotificationCompat.PRIORITY_HIGH)
    notificationManager.notify(NOTIFICATION_ID, builder.build())
}
