package com.greenie.app.common.audioanalyze

import android.content.Context
import android.util.Log
import com.greenie.app.common.audioanalyze.AudioRecordManager.SAMPLE_RATE
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.tensorflow.lite.task.audio.classifier.AudioClassifier
import org.tensorflow.lite.task.core.BaseOptions
import java.io.File
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.SortedMap
import javax.inject.Inject

val SPLIT_SIZE = SAMPLE_RATE * 1
const val DECIBEL_CUT_LINE = 40.0f

class TensorflowHelper @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    private val baseOptions = BaseOptions.builder()
        .setNumThreads(4)
        .useNnapi()

    private val options = AudioClassifier.AudioClassifierOptions.builder()
        .setScoreThreshold(DISPLAY_THRESHOLD)
        .setMaxResults(DEFAULT_NUM_OF_RESULTS)
        .setBaseOptions(baseOptions.build())
        .build()

    private val audioClassifier = AudioClassifier.createFromFileAndOptions(
        context,
        YAMNET_MODEL,
        options
    )

    fun analyzeAudio(wavFile: File): Flow<SortedMap<String, Int>> = flow {
        val resultHashMap = HashMap<String, Int>()

        val tensorAudio = audioClassifier.createInputTensorAudio()

        val inputStream = wavFile.inputStream()
        inputStream.skip(44)
        while (inputStream.available() > 0) {
            val byteArray = ByteArray(SPLIT_SIZE * 2)
            inputStream.read(byteArray)
            val shortArray = ShortArray(byteArray.size / 2)
            ByteBuffer.wrap(byteArray).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer()
                .get(shortArray)

            if (AudioRecordManager.calculateDecibel(shortArray) < DECIBEL_CUT_LINE) {
                continue
            }

            tensorAudio.load(shortArray, 0, shortArray.size)
            val output = audioClassifier.classify(tensorAudio)

            for (classification in output) {
                for (label in classification.categories) {
                    if (resultHashMap.containsKey(label.label)) {
                        resultHashMap[label.label] = resultHashMap[label.label]!! + 1
                    } else {
                        resultHashMap[label.label] = 1
                    }
                }
            }
        }

        val sortedResult = resultHashMap.toSortedMap { key1, key2 ->
            resultHashMap[key2]!!.compareTo(resultHashMap[key1]!!)
        }

        sortedResult.forEach {
            Log.d("TensorflowHelper", "Result: ${it.key} - ${it.value}")
        }

        emit(sortedResult)
    }

    companion object {
        const val DISPLAY_THRESHOLD = 0.92f
        const val DEFAULT_NUM_OF_RESULTS = 4
        const val YAMNET_MODEL = "noises_model.tflite"
    }
}