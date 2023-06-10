package com.greenie.app.common.audioanalyze

import android.content.Context
import android.util.Log
import com.greenie.app.common.audioanalyze.AudioRecordManager.SAMPLE_RATE
import com.greenie.app.core.model.NoiseCategoryEnum
import com.greenie.app.core.model.findCategoryByIndex
import com.greenie.app.core.model.findCategoryByLabel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import org.tensorflow.lite.task.audio.classifier.AudioClassifier
import org.tensorflow.lite.task.core.BaseOptions
import java.io.File
import java.nio.ByteBuffer
import java.nio.ByteOrder
import javax.inject.Inject
import kotlin.time.ExperimentalTime
import kotlin.time.measureTimedValue

const val SPLIT_TIME = 0.1f
const val SKIP_TIME = 0.7f
val SPLIT_SHORT_SIZE = (SAMPLE_RATE * SPLIT_TIME).toInt()
val SPLIT_BYTE_SIZE = SPLIT_SHORT_SIZE * 2
val SKIP_BYTE_SIZE = (SAMPLE_RATE * SKIP_TIME).toInt() * 2
const val DECIBEL_CUT_LINE = 39.0f

const val CALCULATE_THRESHOLD = 0.56f
const val DEFAULT_NUM_OF_RESULTS = 5
const val YAMNET_MODEL = "noises_model.tflite"

class TensorflowHelper @Inject constructor(
    @ApplicationContext private val context: Context,
) {
    private val baseOptions = BaseOptions.builder()
        .setNumThreads(-1)
        .useNnapi()

    private val options = AudioClassifier.AudioClassifierOptions.builder()
        .setScoreThreshold(CALCULATE_THRESHOLD)
        .setMaxResults(DEFAULT_NUM_OF_RESULTS)
        .setBaseOptions(baseOptions.build())
        .build()

    private val audioClassifier = AudioClassifier.createFromFileAndOptions(
        context,
        YAMNET_MODEL,
        options
    )

    @OptIn(ExperimentalTime::class)
    fun analyzeAudio(wavFile: File): Flow<Map<NoiseCategoryEnum, Int>> = flow {
        var count = 0

        val duration = measureTimedValue {
            val resultHashMap = HashMap<NoiseCategoryEnum, Int>()

            val tensorAudio = audioClassifier.createInputTensorAudio()

            val inputStream = wavFile.inputStream()
            inputStream.skip(44)
            while (inputStream.available() > 0) {
                count++

                val byteArray = ByteArray(SPLIT_BYTE_SIZE)
                inputStream.read(byteArray)
                inputStream.skip(SKIP_BYTE_SIZE.toLong())
                val shortArray = ShortArray(SPLIT_SHORT_SIZE)
                ByteBuffer.wrap(byteArray).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer()
                    .get(shortArray)

                if (AudioRecordManager.calculateDecibel(shortArray) < DECIBEL_CUT_LINE) {
                    continue
                }

                tensorAudio.load(shortArray, 0, shortArray.size)
                val output = audioClassifier.classify(tensorAudio)

                /**
                 * Find category by index
                 */
                /**
                 * Find category by index
                 */
                val categorySet = HashSet<NoiseCategoryEnum>()

                for (category in output[0].categories) {
                    val categoryName = findCategoryByIndex(category.index)
                    categorySet.add(categoryName)
//                Log.d("TensorflowHelper", "$count categories0: ${output[0].categories} ${categoryName}")
                }

                /**
                 * Find category by label
                 */

                /**
                 * Find category by label
                 */
                for (category in output[1].categories) {
                    val categoryName = findCategoryByLabel(category.label)
                    categorySet.add(categoryName)
//                Log.d("TensorflowHelper", "$count categories1: ${output[1].categories} ${categoryName}")
                }

                /**
                 * Count category
                 */

                /**
                 * Count category
                 */
                for (category in categorySet) {
                    if (resultHashMap.containsKey(category)) {
                        resultHashMap[category] = resultHashMap[category]!! + 1
                    } else {
                        resultHashMap[category] = 1
                    }
                }
            }

            val valueSum = resultHashMap.values.sum()
            val sortedMap = resultHashMap
                .mapValues { (_, value) -> value * 100 / valueSum }
                .toList()
                .sortedByDescending { (_, value) -> value }
                .toMap()

            Log.d("TensorflowHelper", "SortedMap: $sortedMap")

            emit(sortedMap)
        }
        Log.d("TensorflowHelper", "${count * (SKIP_TIME + SPLIT_TIME)}초간 녹음, Elapsed time: ${duration.duration.inWholeSeconds}초")
    }.flowOn(Dispatchers.Default)
}