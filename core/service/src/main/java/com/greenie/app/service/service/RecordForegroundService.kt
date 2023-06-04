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
import com.greenie.app.common.audioanalyze.AudioRecordManager
import com.greenie.app.common.audioanalyze.TensorflowHelper
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.max
import kotlin.math.min


private const val RECORD_SERVICE_NOTIFICATION_ID = 1

@AndroidEntryPoint
class RecordForegroundService : Service() {

    @Inject
    @RecordServiceNotificationChannelId
    lateinit var serviceNotificationChannelId: String

    @Inject
    @ApplicationContext
    lateinit var context: Context

    @Inject
    lateinit var tensorflowHelper: TensorflowHelper

    private var serviceJob: Job? = null

    private val fileName = "record_${System.currentTimeMillis()}.pcm"
    private val audioShortBufferList = mutableListOf<Short>()

    override fun onCreate() {
        super.onCreate()
        startForeground(RECORD_SERVICE_NOTIFICATION_ID, createNotification())
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            RecordServiceAction.START_RECORDING.action -> {
                if (serviceJob?.isActive == true) {
                    return START_NOT_STICKY
                }

                serviceJob = CoroutineScope(Dispatchers.IO).launch {
                    val audioRecordDataflow = AudioRecordManager.startRecording()
                    val initialValue = audioRecordDataflow.first()

                    var decibelValue = AudioRecordManager.calculateDecibel(initialValue).toFloat()
                    var minimumDecibel = decibelValue
                    var maximumDecibel = decibelValue
                    var averageDecibel: Float
                    val decibelList = mutableListOf<Float>()

                    audioRecordDataflow.collectLatest { byteArray ->
                        /**
                         * Calculate decibel value and update RecordServiceDataFlow
                         */
                        decibelValue = AudioRecordManager.calculateDecibel(byteArray).toFloat()
                        if (decibelValue < 0) {
                            return@collectLatest
                        }
                        decibelList.add(decibelValue)
                        minimumDecibel = min(minimumDecibel, decibelValue)
                        maximumDecibel = max(maximumDecibel, decibelValue)
                        averageDecibel = decibelList.average().toFloat()

                        _recordServiceDataSharedFlow.emit(
                            RecordServiceData(
                                fileName = fileName,
                                isRecording = true,
                                isSaving = false,
                                hasRecord = true,
                                decibelValue = decibelValue,
                                minimumDecibel = minimumDecibel,
                                maximumDecibel = maximumDecibel,
                                averageDecibel = averageDecibel,
                            )
                        )

                        /**
                         * Save byte array to list
                         */
                        audioShortBufferList.addAll(byteArray.toList())
                        if (audioShortBufferList.size > 500000) {
                            AudioRecordManager.saveShortArrayFile(
                                context,
                                fileName,
                                audioShortBufferList.toShortArray()
                            )
                            audioShortBufferList.clear()
                        }
                    }
                }
            }


            RecordServiceAction.PAUSE_RECORDING.action -> {
                if (serviceJob?.isActive == true) {
                    serviceJob?.cancel()
                }
                AudioRecordManager.pauseRecording()
                _recordServiceDataSharedFlow.replayCache.lastOrNull()?.let {
                    _recordServiceDataSharedFlow.tryEmit(
                        it.copy(
                            decibelValue = 0f,
                            isRecording = false,
                            hasRecord = true,
                        )
                    )
                }
            }

            RecordServiceAction.SAVE_RECORDING.action -> {
                CoroutineScope(Dispatchers.IO).launch {
                    if (serviceJob?.isActive == true) {
                        serviceJob?.cancelAndJoin()
                    }
                    _recordServiceDataSharedFlow.replayCache.lastOrNull()?.let {
                        _recordServiceDataSharedFlow.emit(
                            it.copy(
                                isSaving = true,
                            )
                        )
                    }
                    AudioRecordManager.stopRecording()
                    if (audioShortBufferList.isNotEmpty()) {
                        AudioRecordManager.saveShortArrayFile(
                            context,
                            fileName,
                            audioShortBufferList.toShortArray()
                        )
                    }
                    val rawFile = AudioRecordManager.getRecordFile(context, fileName)
                    AudioRecordManager.rawToWave(context, rawFile)
                    rawFile.delete()
                    _recordServiceDataSharedFlow.replayCache.lastOrNull()?.let {
                        _recordServiceDataSharedFlow.emit(
                            it.copy(
                                decibelValue = 0f,
                                isRecording = false,
                                isSaving = false,
                                hasRecord = false,
                            )
                        )
                    }
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

    private fun createNotification(leftTime: Long? = null) =
        NotificationCompat.Builder(this, serviceNotificationChannelId)
            .setContentTitle(getString(R.string.record_service_notification_title))
            .run {
                if (leftTime != null) {
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
            replay = 1,
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
                action = RecordServiceAction.SAVE_RECORDING.action
            }

            context.startService(pendingIntent)
        }

        fun saveRecord(context: Context) {
            serviceState = RecordServiceState.Saving
            val pendingIntent = RecordServiceIntent(context).apply {
                action = RecordServiceAction.SAVE_RECORDING.action
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

private enum class RecordDelay(val delay: Long) {
    TRACKING(1000L),
}

private enum class RecordServiceAction(val action: String) {
    START_RECORDING("START_RECORDING"),
    PAUSE_RECORDING("PAUSE_RECORDING"),
    SAVE_RECORDING("STOP_RECORDING"),
}