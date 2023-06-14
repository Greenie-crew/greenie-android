package com.greenie.app.service.service

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.CountDownTimer
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
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

private const val TRACKING_SERVICE_NOTIFICATION_ID = 2

private const val TRACKING_TIME_LIMIT = 50 * 60 * 1000L
private const val LATENCY_TIME = 100L

private data class DecibelPerMinute(val minute: Int, val decibel: Float)

@AndroidEntryPoint
class TrackingForegroundService : Service() {

    @Inject
    @RecordServiceNotificationChannelId
    lateinit var serviceNotificationChannelId: String

    @Inject
    lateinit var recordFileManager: RecordFileManager

    private var serviceJob: Job? = null

    private var leftTime = TRACKING_TIME_LIMIT
    private val loudNoiseHistory = mutableListOf<NoiseHistoryData>()

    private lateinit var timer: CountDownTimer

    private var temporaryHighestDecibelPerMinute = DecibelPerMinute(
        Calendar.getInstance().get(Calendar.MINUTE),
        0f
    )

    override fun onCreate() {
        super.onCreate()
        startForeground(TRACKING_SERVICE_NOTIFICATION_ID, createNotification(leftTime))
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            TrackerServiceAction.START_TRACKING.action -> {
                if (serviceJob?.isActive == true) {
                    return START_STICKY
                }
                timer = object : CountDownTimer(leftTime, 1000) {
                    override fun onTick(millisUntilFinished: Long) {
                        leftTime = millisUntilFinished
                        _trackingServiceDataSharedFlow.tryEmit(
                            TrackingServiceData(
                                serviceState = TrackingServiceState.TRACKING,
                                leftTime = leftTime,
                                loudNoiseHistory = loudNoiseHistory,
                            )
                        )
                        startForeground(
                            TRACKING_SERVICE_NOTIFICATION_ID,
                            createNotification(leftTime)
                        )
                    }

                    override fun onFinish() {
                        stopSelf()
                    }
                }
                timer.start()
                serviceJob = CoroutineScope(Dispatchers.IO).launch {
                    val calendar = Calendar.getInstance()
                    val audioRecordDataflow = AudioRecordManager.startRecording(LATENCY_TIME)
                    audioRecordDataflow.collectLatest { byteArray ->
                        /**
                         * Calculate decibel value and update RecordServiceDataFlow
                         */
                        val decibelValue = AudioRecordManager.calculateDecibel(byteArray).toFloat()
                        temporaryHighestDecibelPerMinute =
                            if (decibelValue > temporaryHighestDecibelPerMinute.decibel) {
                                DecibelPerMinute(
                                    temporaryHighestDecibelPerMinute.minute,
                                    decibelValue
                                )
                            } else {
                                DecibelPerMinute(
                                    temporaryHighestDecibelPerMinute.minute,
                                    temporaryHighestDecibelPerMinute.decibel
                                )
                            }
                        calendar.timeInMillis = System.currentTimeMillis()
                        val minute = calendar.get(Calendar.MINUTE)
                        if (minute != temporaryHighestDecibelPerMinute.minute) {
                            loudNoiseHistory.add(
                                NoiseHistoryData(
                                    time = System.currentTimeMillis() - 60000,
                                    decibel = temporaryHighestDecibelPerMinute.decibel
                                )
                            )
                            temporaryHighestDecibelPerMinute = DecibelPerMinute(
                                minute,
                                0f
                            )
                        }
                    }
                }
            }

            TrackerServiceAction.PAUSE_TRACKING.action -> {
                if (serviceJob?.isActive == true) {
                    serviceJob?.cancel()
                }
                timer.cancel()
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
                stopSelf()
                stopService(intent)
            }
        }

        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        timer.cancel()
        CoroutineScope(Dispatchers.IO).launch {
            if (serviceJob?.isActive == true) {
                serviceJob?.cancelAndJoin()
            }
            AudioRecordManager.stopRecording()
            loudNoiseHistory.add(
                NoiseHistoryData(
                    time = System.currentTimeMillis() - 60000,
                    decibel = temporaryHighestDecibelPerMinute.decibel
                )
            )
            _trackingServiceDataSharedFlow.emit(
                TrackingServiceData(
                    serviceState = TrackingServiceState.END,
                    leftTime = leftTime,
                    loudNoiseHistory = loudNoiseHistory,
                )
            )
        }
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    private fun createNotification(leftTime: Long) =
        NotificationCompat.Builder(this, serviceNotificationChannelId)
            .setContentTitle(getString(R.string.record_service_notification_title))
            .run {
                setContentText(
                    getString(
                        R.string.record_service_notification_content,
                        String.format(
                            "%02d:%02d:%02d",
                            leftTime / 1000 / 60 / 60,
                            leftTime / 1000 / 60 % 60,
                            leftTime / 1000 % 60
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