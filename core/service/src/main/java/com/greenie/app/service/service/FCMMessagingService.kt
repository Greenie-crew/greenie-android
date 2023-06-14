package com.greenie.app.service.service

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class FCMMessagingService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        Log.d("onNewToken","${token}")
        super.onNewToken(token)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        Log.d("onMessageReceived","${remoteMessage.notification?.title}")

        super.onMessageReceived(remoteMessage)
    }
}