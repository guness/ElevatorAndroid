package com.guness.elevator.service

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.guness.elevator.message.AbstractMessage
import timber.log.Timber

/**
 * Created by guness on 21.01.2018.
 */

class SGFirebaseMessagingService : FirebaseMessagingService() {
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        Timber.d("MessageReceived: %s", remoteMessage)
        val text = remoteMessage.data["protocol"]
        val message: AbstractMessage? = try {
            AbstractMessage.GSON.fromJson(text, AbstractMessage::class.java)
        } catch (e: Exception) {
            Timber.e(e, "Cannot parse message: $text")
            null
        }
    }
}
