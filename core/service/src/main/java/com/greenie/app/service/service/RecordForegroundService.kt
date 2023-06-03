package com.greenie.app.service.service

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationCompat.FOREGROUND_SERVICE_IMMEDIATE
import com.greenie.app.core.model.RecordServiceData
import com.greenie.app.service.R
import com.greenie.app.service.di.RecordServiceNotificationChannelId
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject

private const val RECORD_SERVICE_NOTIFICATION_ID = 1

@AndroidEntryPoint
class RecordForegroundService : Service() {

    @Inject
    @RecordServiceNotificationChannelId
    lateinit var serviceNotificationChannelId: String

    @ApplicationContext
    lateinit var context: Context

    override fun onCreate() {
        super.onCreate()
        startForeground(RECORD_SERVICE_NOTIFICATION_ID, createNotification())
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            RecordServiceAction.START_RECORDING.action -> {
                _recordServiceDataSharedFlow.tryEmit(
                    RecordServiceData(
                        decibelValue = 120.901f,
                        isRecording = true,
                        hasRecord = true,
                        minimumDecibel = 0f,
                        maximumDecibel = 0f,
                        averageDecibel = 0f,
                    )
                )

            }

            RecordServiceAction.PAUSE_RECORDING.action -> {
                _recordServiceDataSharedFlow.tryEmit(
                    RecordServiceData(
                        decibelValue = 0f,
                        isRecording = false,
                        hasRecord = true,
                        minimumDecibel = 0f,
                        maximumDecibel = 0f,
                        averageDecibel = 0f,
                    )
                )
            }

            RecordServiceAction.STOP_RECORDING.action -> {
                _recordServiceDataSharedFlow.tryEmit(
                    RecordServiceData(
                        decibelValue = 0f,
                        isRecording = false,
                        hasRecord = false,
                        minimumDecibel = 0f,
                        maximumDecibel = 0f,
                        averageDecibel = 0f,
                    )
                )
                stopSelf()
                stopService(intent)
            }
        }

        return super.onStartCommand(intent, flags, startId)
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    private fun createNotification(leftTime: Long? = null) =
        NotificationCompat.Builder(this, serviceNotificationChannelId)
            .setContentTitle(getString(R.string.record_service_notification_title))
            .run {
                if (leftTime != null) {
                    val leftTimeSecond = leftTime/1000
                    setContentText(getString(R.string.record_service_notification_content, String.format("%02d:%02d:%02d", leftTimeSecond / 3600, (leftTimeSecond % 3600) / 60, leftTimeSecond % 60)))
                }
                this
            }
            .setSmallIcon(R.drawable.ic_service)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setCategory(NotificationCompat.CATEGORY_SERVICE)
            .setOnlyAlertOnce(true)
            .setOngoing(true)
            .setAutoCancel(true)
            .setForegroundServiceBehavior(FOREGROUND_SERVICE_IMMEDIATE)
            .build()

    companion object {
        private var serviceState: RecordServiceState = RecordServiceState.Idle
        private val _recordServiceDataSharedFlow = MutableSharedFlow<RecordServiceData>(
            extraBufferCapacity = 1,
            onBufferOverflow = BufferOverflow.DROP_OLDEST
        )
        val recordServiceDataSharedFlow: SharedFlow<RecordServiceData> =
            _recordServiceDataSharedFlow.asSharedFlow()

        private var RecordServiceIntent = { context: Context ->
            Intent(context, RecordForegroundService::class.java)
        }

        fun startRecordService(context: Context) {
            serviceState = RecordServiceState.Recording
            val pendingIntent = RecordServiceIntent(context).apply {
                action = RecordServiceAction.START_RECORDING.action
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(pendingIntent)
            } else {
                context.startService(pendingIntent)
            }
        }

        fun pauseRecordService(context: Context) {
            serviceState = RecordServiceState.Paused
            val pendingIntent = RecordServiceIntent(context).apply {
                action = RecordServiceAction.PAUSE_RECORDING.action
            }

            context.startService(pendingIntent)
        }

        fun stopRecordService(context: Context) {
            serviceState = RecordServiceState.Idle
            val pendingIntent = RecordServiceIntent(context).apply {
                action = RecordServiceAction.STOP_RECORDING.action
            }

            context.startService(pendingIntent)
        }

        fun saveRecord(context: Context) {
            serviceState = RecordServiceState.Saving
            val pendingIntent = RecordServiceIntent(context).apply {
                action = RecordServiceAction.STOP_RECORDING.action
            }

            context.startService(pendingIntent)
        }

        fun analyzeRecord(context: Context) {
            serviceState = RecordServiceState.Analyzing
            val pendingIntent = RecordServiceIntent(context).apply {
                action = RecordServiceAction.STOP_RECORDING.action
            }

            context.startService(pendingIntent)
        }
    }
}

sealed interface RecordServiceState {
    object Idle : RecordServiceState
    object Recording : RecordServiceState
    object Paused : RecordServiceState
    object Analyzing : RecordServiceState
    object Saving : RecordServiceState
}

private enum class RecordTime(val time: Long) {
    TRACKING(10800000L),
}

private enum class RecordServiceAction(val action: String) {
    START_RECORDING("START_RECORDING"),
    PAUSE_RECORDING("PAUSE_RECORDING"),
    STOP_RECORDING("STOP_RECORDING"),
}