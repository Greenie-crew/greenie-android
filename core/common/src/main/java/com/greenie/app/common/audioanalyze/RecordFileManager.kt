package com.greenie.app.common.audioanalyze

import android.content.Context
import android.os.Environment
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.DataOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.nio.ByteBuffer
import java.nio.ByteOrder
import javax.inject.Inject

class RecordFileManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    fun saveShortArrayFile(fileName: String, shortArray: ShortArray): File {
        val byteArray = ByteArray(shortArray.size * 2)
        ByteBuffer.wrap(byteArray).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().put(shortArray)

        val file = File(context.getExternalFilesDir(Environment.DIRECTORY_MUSIC), fileName)
        val fileOutputStream = FileOutputStream(file, true)
        fileOutputStream.write(byteArray)
        fileOutputStream.close()
        return file
    }

    fun getRecordFile(fileName: String): File? {
        val file = File(context.getExternalFilesDir(Environment.DIRECTORY_MUSIC), fileName)
        if (!file.exists()) {
            return null
        }
        return File(context.getExternalFilesDir(Environment.DIRECTORY_MUSIC), fileName)
    }

    fun getRecordFileList(): List<File> {
        return context.getExternalFilesDir(Environment.DIRECTORY_MUSIC)?.listFiles()?.toList() ?: emptyList()
    }

    @Throws(IOException::class)
    fun rawToWave(rawFile: File): File {
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
            writeInt(output, AudioRecordManager.SAMPLE_RATE) // sample rate
            writeInt(output, 16) // byte rate
            writeShort(output, 2.toShort()) // block align
            writeShort(output, 16.toShort()) // bits per sample
            writeString(output, "data") // subchunk 2 id
            writeInt(output, rawFile.length().toInt()) // subchunk 2 size

            inputStream.copyTo(output, AudioRecordManager.BUFFER_SIZE_RECORDING)
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