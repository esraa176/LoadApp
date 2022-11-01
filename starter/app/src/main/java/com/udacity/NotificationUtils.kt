package com.udacity

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.core.app.NotificationCompat

private const val download_channel_id: String = "channelId"
private const val NOTIFICATION_ID = 0

fun NotificationManager.sendNotification(messageBody: String, applicationContext: Context, title: String, downloadStatus: String) {

    // Create the content intent for the notification, which launches detail activity
    val contentIntent = Intent(applicationContext, DetailActivity::class.java)

    // Adding all the key/value pairs from the provided bundle to the intent
    contentIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
    contentIntent.putExtras(
        withExtras(
            title = title,
            downloadStatus = downloadStatus
        )
    )

    // Create PendingIntent so the system uses it to launch the app
    val contentPendingIntent = PendingIntent.getActivity(
        applicationContext,
        NOTIFICATION_ID,
        contentIntent,
        PendingIntent.FLAG_UPDATE_CURRENT //use the existing PendingIntent
    )


    // get an instance of NotificationCompat.Builder
    val builder = NotificationCompat.Builder(
        applicationContext,
        download_channel_id
    ).setSmallIcon(R.drawable.ic_assistant_black_24dp)
        .setContentTitle(
            applicationContext
                .getString(R.string.notification_title)
        )
        .setContentText(messageBody)
        .setContentIntent(contentPendingIntent)
        .setAutoCancel(true)  //so the notification dismisses itself after being clicked
        .setPriority(NotificationCompat.PRIORITY_HIGH)
            // added an action button to the displayed notification, which, upon clicking, opens the details screen
        .addAction(
            R.drawable.ic_assistant_black_24dp,
            applicationContext.getString(R.string.notification_button),
            contentPendingIntent)

    // Deliver the notification
    notify(NOTIFICATION_ID, builder.build())
}

fun NotificationManager.cancelNotifications() {
    cancelAll()
}


private const val EXTRA_TITLE = "notification_title"
private const val EXTRA_STATUS = "notification_status"

fun withExtras(title: String, downloadStatus: String): Bundle {
    return Bundle().apply {
        putString(EXTRA_TITLE, title)
        putString(EXTRA_STATUS, downloadStatus)
    }
}
