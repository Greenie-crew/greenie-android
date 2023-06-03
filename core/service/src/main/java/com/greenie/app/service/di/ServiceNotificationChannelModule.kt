package com.greenie.app.service.di

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.os.Build
import com.greenie.app.service.R
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class RecordServiceNotificationChannelId

@Module
@InstallIn(SingletonComponent::class)
object ServiceNotificationChannelModule {

    private const val RECORD_SERVICE_NOTIFICATION_CHANNEL_ID =
        "RECORD_SERVICE_NOTIFICATION_CHANNEL_ID"

    private fun createNotificationChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            with(context) {
                val name = getString(R.string.record_service_channel_name)
                val description = getString(R.string.record_service_channel_description)
                val importance = NotificationManager.IMPORTANCE_DEFAULT
                val channel = NotificationChannel(RECORD_SERVICE_NOTIFICATION_CHANNEL_ID, name, importance)
                channel.description = description
                val notificationManager = getSystemService(Service.NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.createNotificationChannel(channel)
            }
        }
    }

    @Provides
    @RecordServiceNotificationChannelId
    fun provideRecordServiceNotificationChannelId(
        @ApplicationContext context: Context,
    ): String {
        createNotificationChannel(context)
        return RECORD_SERVICE_NOTIFICATION_CHANNEL_ID
    }
}