package com.greenie.app.service.service

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.greenie.app.common.audioanalyze.AudioRecordManager
import com.greenie.app.common.audioanalyze.RecordFileManager
import com.greenie.app.core.model.NoiseHistoryData
import com.greenie.app.core.model.TrackingServiceData
import com.greenie.app.core.model.TrackingServiceState
import com.greenie.app.service.R
import com.greenie.app.service.di.RecordServiceNotificationChannelId
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TRACKING_SERVICE_NOTIFICATION_ID = 2
private const val TRACKING_TIME_LIMIT = 4 * 60 * 60 * 1000L
private const val TRACKING_THRESHOLDS_DECIBEL = 60f
private const val LATENCY_TIME = 1000L

@AndroidEntryPoint
class TrackingForegroundService : Service() {

    @Inject
    @RecordServiceNotificationChannelId
    lateinit var serviceNotificationChannelId: String

    @Inject
    lateinit var recordFileManager: RecordFileManager

    private var serviceJob: Job? = null


    private var startTime = 0L
    private var leftTime = TRACKING_TIME_LIMIT
    private val loudNoiseHistory = mutableListOf<NoiseHistoryData>()

    override fun onCreate() {
        super.onCreate()
        startForeground(TRACKING_SERVICE_NOTIFICATION_ID, createNotification(leftTime))
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            TrackerServiceAction.START_TRACKING.action -> {
                if (serviceJob?.isActive == true) {
                    return START_STICKY
                }
                startTime = System.currentTimeMillis()
                serviceJob = CoroutineScope(Dispatchers.IO).launch {
                    val audioRecordDataflow = AudioRecordManager.startRecording(LATENCY_TIME)
                    audioRecordDataflow.collectLatest { byteArray ->
                        /**
                         * Calculate decibel value and update RecordServiceDataFlow
                         */
                        val decibelValue = AudioRecordManager.calculateDecibel(byteArray).toFloat()
                        if (decibelValue > TRACKING_THRESHOLDS_DECIBEL) {
                            loudNoiseHistory.add(
                                NoiseHistoryData(
                                    time = System.currentTimeMillis(),
                                    decibel = decibelValue
                                )
                            )
                        }

                        _trackingServiceDataSharedFlow.emit(
                            TrackingServiceData(
                                serviceState = TrackingServiceState.TRACKING,
                                leftTime = leftTime,
                                loudNoiseHistory = loudNoiseHistory,
                            )
                        )

                        leftTime -= (System.currentTimeMillis() - startTime)

                        if (leftTime <= 0) {
                            stopSelf()
                            stopService(intent)
                        }
                        startForeground(TRACKING_SERVICE_NOTIFICATION_ID, createNotification(leftTime))
                    }
                }
            }


            TrackerServiceAction.PAUSE_TRACKING.action -> {
                if (serviceJob?.isActive == true) {
                    serviceJob?.cancel()
                }
                AudioRecordManager.pauseRecording()
                _trackingServiceDataSharedFlow.tryEmit(
                    TrackingServiceData(
                        serviceState = TrackingServiceState.PAUSE,
                        leftTime = leftTime,
                        loudNoiseHistory = loudNoiseHistory,
                    )
                )
            }

            TrackerServiceAction.END_TRACKING.action -> {
                CoroutineScope(Dispatchers.IO).launch {
                    _trackingServiceDataSharedFlow.replayCache.lastOrNull()?.let { serviceData ->
                        _trackingServiceDataSharedFlow.emit(
                            serviceData.copy(
                                serviceState = TrackingServiceState.END,
                            )
                        )
                    }
                    if (serviceJob?.isActive == true) {
                        serviceJob?.cancelAndJoin()
                    }
                    AudioRecordManager.stopRecording()
                    stopSelf()
                    stopService(intent)
                }
            }
        }

        return super.onStartCommand(intent, flags, startId)
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    private fun createNotification(leftTime: Long) =
        NotificationCompat.Builder(this, serviceNotificationChannelId)
            .setContentTitle(getString(R.string.record_service_notification_title))
            .run {
                val leftTimeSecond = leftTime / 1000
                setContentText(
                    getString(
                        R.string.record_service_notification_content,
                        String.format(
                            "%02d:%02d:%02d",
                            leftTimeSecond / 3600,
                            (leftTimeSecond % 3600) / 60,
                            leftTimeSecond % 60
                        )
                    )
                )
                this
            }
            .setSmallIcon(R.drawable.ic_service)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setCategory(NotificationCompat.CATEGORY_SERVICE)
            .setOnlyAlertOnce(true)
            .setOngoing(true)
            .setAutoCancel(true)
            .setForegroundServiceBehavior(NotificationCompat.FOREGROUND_SERVICE_IMMEDIATE)
            .build()

    companion object {
        private val _trackingServiceDataSharedFlow = MutableSharedFlow<TrackingServiceData>(
            replay = 1,
            onBufferOverflow = BufferOverflow.DROP_OLDEST
        )
        val trackingServiceDataSharedFlow: SharedFlow<TrackingServiceData> =
            _trackingServiceDataSharedFlow.asSharedFlow()

        private var TrackingServiceIntent = { context: Context ->
            Intent(context, TrackingForegroundService::class.java)
        }

        fun startTrackingService(context: Context) {
            val pendingIntent = TrackingServiceIntent(context).apply {
                action = TrackerServiceAction.START_TRACKING.action
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(pendingIntent)
            } else {
                context.startService(pendingIntent)
            }
        }

        fun pauseTrackingService(context: Context) {
            val pendingIntent = TrackingServiceIntent(context).apply {
                action = TrackerServiceAction.PAUSE_TRACKING.action
            }

            context.startService(pendingIntent)
        }

        fun stopTrackingService(context: Context) {
            val pendingIntent = TrackingServiceIntent(context).apply {
                action = TrackerServiceAction.END_TRACKING.action
            }

            context.startService(pendingIntent)
        }
    }
}

private enum class TrackerServiceAction(val action: String) {
    START_TRACKING("START_TRACKING"),
    PAUSE_TRACKING("PAUSE_TRACKING"),
    END_TRACKING("END_TRACKING"),
}