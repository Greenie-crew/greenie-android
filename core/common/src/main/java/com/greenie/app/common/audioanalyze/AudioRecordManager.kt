package com.greenie.app.common.audioanalyze

import android.annotation.SuppressLint
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlin.math.log10
import kotlin.math.sqrt

const val DECIBEL_ADJUSTMENT = 5.0f

object AudioRecordManager {
    // for raw audio, use MediaRecorder.AudioSource.UNPROCESSED, see note in MediaRecorder section
    private const val AUDIO_SOURCE = MediaRecorder.AudioSource.MIC

    private val SAMPLE_RATE_LIST = arrayOf(
//        44100,
//        22050,
        16000,
        11025,
        8000
    )
    val SAMPLE_RATE = SAMPLE_RATE_LIST.find { sampleRate ->
        AudioRecord.getMinBufferSize(
            sampleRate,
            CHANNEL_CONFIG,
            AUDIO_FORMAT
        ) != AudioRecord.ERROR_BAD_VALUE
    } ?: run {
        throw IllegalArgumentException("Invalid sample rate")
    }
    private const val CHANNEL_CONFIG = AudioFormat.CHANNEL_IN_MONO
    private const val AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT
    internal val BUFFER_SIZE_RECORDING =
        AudioRecord.getMinBufferSize(SAMPLE_RATE, CHANNEL_CONFIG, AUDIO_FORMAT)
    private val SHORT_BUFFER_SIZE = BUFFER_SIZE_RECORDING / 2

    private var audioRecord: AudioRecord? = null

    @SuppressLint("MissingPermission")
    fun startRecording(): Flow<ShortArray> = flow {
        audioRecord = AudioRecord(
            AUDIO_SOURCE,
            SAMPLE_RATE,
            CHANNEL_CONFIG,
            AUDIO_FORMAT,
            BUFFER_SIZE_RECORDING
        )
        audioRecord?.startRecording()

        val shortBuffer = ShortArray(SHORT_BUFFER_SIZE)

        while (true) {
            audioRecord?.read(shortBuffer, 0, SHORT_BUFFER_SIZE, AudioRecord.READ_BLOCKING)
            if (shortBuffer.first() > 0) {
                emit(shortBuffer)
                break
            }
        }

        while (audioRecord?.recordingState == AudioRecord.RECORDSTATE_RECORDING) {
            audioRecord?.read(shortBuffer, 0, SHORT_BUFFER_SIZE, AudioRecord.READ_BLOCKING)
            emit(shortBuffer)
        }
    }

    fun pauseRecording() {
        audioRecord?.stop()
    }

    fun stopRecording() {
        audioRecord?.stop()
        audioRecord?.release()
        audioRecord = null
    }

    fun calculateDecibel(buffer: ShortArray): Double {
        var sum = 0.0
        for (i in buffer.indices) {
            sum += buffer[i] * buffer[i]
        }
        val rms = sqrt(sum / buffer.size)
        return 20 * log10(rms) + DECIBEL_ADJUSTMENT
    }
}