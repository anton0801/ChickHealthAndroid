package com.helathchickapp.chickhealth

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.os.bundleOf
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlin.collections.contains



class ChickHealthPushService : FirebaseMessagingService() {

    companion object {
        private const val CHICK_HEALTH_CHANNEL_ID = "chick_health_notifications"
        private const val CHICK_HEALTH_CHANNEL_NAME = "Chick Health+ Notifications"
        private const val CHICK_HEALTH_NOT_TAG = "CHICK_HEALTH"
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        remoteMessage.notification?.let {
            if (remoteMessage.data.contains("url")) {
                eggLabelShowNotification(it.title ?: CHICK_HEALTH_NOT_TAG, it.body ?: "", data = remoteMessage.data["url"])
            } else {
                eggLabelShowNotification(it.title ?: CHICK_HEALTH_NOT_TAG, it.body ?: "", data = null)
            }
        }

        if (remoteMessage.data.isNotEmpty()) {
            eggLabelHandleDataPayload(remoteMessage.data)
        }
    }

    private fun eggLabelShowNotification(title: String, message: String, data: String?) {
        val eggLabelNotificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Создаем канал уведомлений для Android 8+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHICK_HEALTH_CHANNEL_ID,
                CHICK_HEALTH_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            )
            eggLabelNotificationManager.createNotificationChannel(channel)
        }

        val eggLabelIntent = Intent(this, ChickHealthActivity::class.java).apply {
            putExtras(bundleOf(
                "url" to data
            ))
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        val eggLabelPendingIntent = PendingIntent.getActivity(
            this,
            0,
            eggLabelIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )

        val eggLabelNotification = NotificationCompat.Builder(this, CHICK_HEALTH_CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(message)
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setAutoCancel(true)
            .setContentIntent(eggLabelPendingIntent)
            .build()

        eggLabelNotificationManager.notify(System.currentTimeMillis().toInt(), eggLabelNotification)
    }

    private fun eggLabelHandleDataPayload(data: Map<String, String>) {
        data.forEach { (key, value) ->
            Log.d(CHICK_HEALTH_CHANNEL_ID, "Data key=$key value=$value")
        }
    }

}