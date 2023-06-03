package com.greenie.app.common.tensorflow

import org.tensorflow.lite.task.audio.classifier.AudioClassifier
import org.tensorflow.lite.task.core.BaseOptions

class AudioClassiferHelper {
    val baseOptions = BaseOptions.builder()
        .setNumThreads(2)
        .useNnapi()

    val options = AudioClassifier.AudioClassifierOptions.builder()
        .setScoreThreshold(DISPLAY_THRESHOLD)
        .setMaxResults(DEFAULT_NUM_OF_RESULTS)
        .setBaseOptions(baseOptions.build())
        .build()



    companion object {
        const val DISPLAY_THRESHOLD = 0.3f
        const val DEFAULT_NUM_OF_RESULTS = 2
        const val DEFAULT_OVERLAP_VALUE = 0.5f
        const val YAMNET_MODEL = "moises_model.tflite"
    }
}