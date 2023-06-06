package com.greenie.app.common.audioanalyze

import android.annotation.SuppressLint
import android.content.Context
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.os.Environment
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.DataOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.nio.ByteBuffer
import java.nio.ByteOrder
import kotlin.math.log10
import kotlin.math.sqrt

object AudioRecordManager {
    // for raw audio, use MediaRecorder.AudioSource.UNPROCESSED, see note in MediaRecorder section
    private const val AUDIO_SOURCE = MediaRecorder.AudioSource.MIC

    private val SAMPLE_RATE_LIST = arrayOf(44100, 22050, 16000, 11025, 8000)
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
    private val BUFFER_SIZE_RECORDING = AudioRecord.getMinBufferSize(SAMPLE_RATE, CHANNEL_CONFIG, AUDIO_FORMAT)
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
        return 20 * log10(rms)
    }

    fun saveShortArrayFile(context: Context, fileName: String, shortArray: ShortArray): File {
        val byteArray = ByteArray(shortArray.size * 2)
        ByteBuffer.wrap(byteArray).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().put(shortArray)

        val file = File(context.getExternalFilesDir(Environment.DIRECTORY_MUSIC), fileName)
        val fileOutputStream = FileOutputStream(file, true)
        fileOutputStream.write(byteArray)
        fileOutputStream.close()
        return file
    }

    fun getRecordFile(context: Context, fileName: String): File {
        return File(context.getExternalFilesDir(Environment.DIRECTORY_MUSIC), fileName)
    }

    fun getRecordFileList(context: Context): List<File> {
        return context.getExternalFilesDir(Environment.DIRECTORY_MUSIC)?.listFiles()?.toList() ?: emptyList()
    }

    @Throws(IOException::class)
    fun rawToWave(context: Context, rawFile: File): File {
        val waveFile = File(
            context.getExternalFilesDir(Environment.DIRECTORY_MUSIC),
            rawFile.nameWithoutExtension + ".wav"
        )

        val inputStream = rawFile.inputStream()

        var output: DataOutputStream? = null
        try {
            output = DataOutputStream(FileOutputStream(waveFile))
            // WAVE header
            // see http://ccrma.stanford.edu/courses/422/projects/WaveFormat/
            writeString(output, "RIFF") // chunk id
            writeInt(output, 36 + rawFile.length().toInt()) // chunk size
            writeString(output, "WAVE") // format
            writeString(output, "fmt ") // subchunk 1 id
            writeInt(output, 16) // subchunk 1 size
            writeShort(output, 1.toShort()) // audio format (1 = PCM)
            writeShort(output, 1.toShort()) // number of channels
            writeInt(output, SAMPLE_RATE) // sample rate
            writeInt(output, 16) // byte rate
            writeShort(output, 2.toShort()) // block align
            writeShort(output, 16.toShort()) // bits per sample
            writeString(output, "data") // subchunk 2 id
            writeInt(output, rawFile.length().toInt()) // subchunk 2 size

            inputStream.copyTo(output, BUFFER_SIZE_RECORDING)
        } finally {
            inputStream.close()
            output?.close()
        }

        return waveFile
    }

    @Throws(IOException::class)
    private fun writeInt(output: DataOutputStream, value: Int) {
        output.write(value shr 0)
        output.write(value shr 8)
        output.write(value shr 16)
        output.write(value shr 24)
    }

    @Throws(IOException::class)
    private fun writeShort(output: DataOutputStream, value: Short) {
        output.write(value.toInt() shr 0)
        output.write(value.toInt() shr 8)
    }

    @Throws(IOException::class)
    private fun writeString(output: DataOutputStream, value: String) {
        for (element in value) {
            output.write(element.code)
        }
    }
}