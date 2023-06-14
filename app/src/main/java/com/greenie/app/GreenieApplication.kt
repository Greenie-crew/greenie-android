package com.greenie.app

import android.app.Application
import com.google.firebase.messaging.FirebaseMessaging
import com.greenie.app.core.domain.usecase.firebase.GetFirebaseToken
import com.greenie.app.core.domain.usecase.firebase.SaveFirebaseToken
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class GreenieApplication : Application() {

    @Inject
    lateinit var saveFirebaseToken: SaveFirebaseToken

    override fun onCreate() {
        super.onCreate()

        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val token = task.result
                token?.let {
                    saveFirebaseToken(it)
                }
            } else {
                saveFirebaseToken("0")
            }
        }
    }
}