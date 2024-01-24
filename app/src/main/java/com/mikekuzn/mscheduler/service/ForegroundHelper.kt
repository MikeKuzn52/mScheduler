package com.mikekuzn.mscheduler.service

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat

private const val NOTIFICATION_ID = 969
private const val CHANNEL_ID = "Channel_ID"
private const val CHANNEL_NAME = "CHANNEL_NAME"

class ForegroundHelper(private val serviceContext: Service) {
    private lateinit var notificationBuilder: NotificationCompat.Builder
    private val notificationManager by lazy { serviceContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager }

    @SuppressLint("ForegroundServiceType")
    fun startForegroundService(set: (NotificationCompat.Builder) -> Unit) {
        notificationBuilder = NotificationCompat.Builder(serviceContext, CHANNEL_ID)
        set(notificationBuilder)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_LOW
            )
            notificationManager.createNotificationChannel(channel)
        }
        serviceContext.startForeground(NOTIFICATION_ID, notificationBuilder.build())
    }

    fun updateNotificationText(text: String) {
        notificationManager.notify(
            NOTIFICATION_ID, notificationBuilder.setContentText(text).build()
        )
    }

    fun stopForegroundService() {
        notificationManager.cancel(NOTIFICATION_ID)
        serviceContext.stopForeground(Service.STOP_FOREGROUND_REMOVE)
        serviceContext.stopSelf()
    }
}