package com.guness.elevator.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.graphics.Color
import android.media.AudioAttributes
import android.net.Uri
import android.os.Build
import android.support.annotation.RequiresApi
import android.support.v4.app.NotificationCompat
import android.support.v4.app.TaskStackBuilder
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.guness.core.SGApplication
import com.guness.elevator.R
import com.guness.elevator.message.AbstractMessage
import com.guness.elevator.message.AbstractMessage.Companion.GSON
import com.guness.elevator.message.inc.UpdateState
import com.guness.elevator.model.ElevatorState
import com.guness.elevator.ui.pages.panel.PanelActivity
import timber.log.Timber

/**
 * Created by guness on 21.01.2018.
 */
class SGFirebaseMessagingService : FirebaseMessagingService() {
    private lateinit var mDing: Uri
    private lateinit var mNotificationManager: NotificationManager

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        mDing = Uri.parse("android.resource://" + packageName + "/" + R.raw.elevator_ding)
        mNotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannel()
        }
        val protocol = remoteMessage.data["protocol"]
        if (protocol == null) {
            Timber.e("Undefined PUSH: " + GSON.toJson(remoteMessage))
        } else {
            Timber.d("MessageReceived: %s", protocol)
            val app = application as SGApplication
            val message: AbstractMessage? = try {
                GSON.fromJson(protocol, AbstractMessage::class.java)
            } catch (e: Exception) {
                Timber.e(e, "Cannot parse message: $protocol")
                null
            }
            when (message) {
                is UpdateState -> {
                    if (message.state?.action == ElevatorState.STOP) {
                        val order = app.getDatabase().dao().getOrder()
                        if (order != null) {
                            if (order.device == message.state?.device && order.floor == message.state?.floor) {
                                app.getDatabase().dao().clearOrder()
                                showDingNotification(order.device, order.floor!!)
                            }
                        }
                    }
                }
                else -> {
                    Timber.e("Unhandled message type: " + message)
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createChannel() {
        val name = getString(R.string.app_name)
        val channel = NotificationChannel(CHANNEL_ID, name, NotificationManager.IMPORTANCE_HIGH)
        channel.description = getString(R.string.channel_arrived_description)
        channel.enableLights(true)
        channel.lightColor = Color.RED
        channel.setShowBadge(true)
        channel.enableVibration(true)
        channel.setSound(mDing, AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                .build())
        channel.vibrationPattern = longArrayOf(100, 200, 300, 400, 500, 400, 300, 200, 400)
        mNotificationManager.createNotificationChannel(channel)
    }

    private fun showDingNotification(device: String, floor: Int) {

        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_hilow)
                .setSound(mDing)
                .setAutoCancel(true)
                .setNumber(floor)
                .setContentTitle(getString(R.string.elevator_arrived))
                .setContentText(getString(R.string.floor_n, floor))

        val resultIntent = PanelActivity.newIntent(this, device)

        val stackBuilder = TaskStackBuilder.create(this)
        stackBuilder.addParentStack(PanelActivity::class.java)
        stackBuilder.addNextIntent(resultIntent)
        val resultPendingIntent = stackBuilder.getPendingIntent(
                0,
                PendingIntent.FLAG_UPDATE_CURRENT
        )
        builder.setContentIntent(resultPendingIntent)
        mNotificationManager.notify(ARRIVED_NOTIFICATION_ID, builder.build())

    }

    companion object {
        private const val ARRIVED_NOTIFICATION_ID = 10101
        private const val CHANNEL_ID = "arrived_channel"
    }
}
