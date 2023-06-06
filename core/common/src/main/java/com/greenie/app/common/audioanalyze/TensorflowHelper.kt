package com.greenie.app.common.audioanalyze

import android.content.Context
import android.util.Log
import com.greenie.app.common.audioanalyze.AudioRecordManager.SAMPLE_RATE
import com.greenie.app.common.audioanalyze.NoiseCategory.NoiseCategoryEnum
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

val SPLIT_SIZE = (SAMPLE_RATE * 0.02f).toInt()
const val DECIBEL_CUT_LINE = 20.0f

const val DISPLAY_THRESHOLD = 0.7f
const val DEFAULT_NUM_OF_RESULTS = 4
const val YAMNET_MODEL = "noises_model.tflite"

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

    fun analyzeAudio(wavFile: File): Flow<Map<NoiseCategoryEnum, Int>> = flow {
        val resultHashMap = HashMap<NoiseCategoryEnum, Int>()

        val tensorAudio = audioClassifier.createInputTensorAudio()

        val inputStream = wavFile.inputStream()
        inputStream.skip(44)
        while (inputStream.available() > 0) {
            val byteArray = ByteArray(SPLIT_SIZE * 2)
            inputStream.read(byteArray)
            val shortArray = ShortArray(SPLIT_SIZE)
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
            val categorySet = HashSet<NoiseCategoryEnum>()

            for (category in output[0].categories) {
                val categoryName = NoiseCategory.findCategoryByIndex(category.index)
                categorySet.add(categoryName)
            }
            Log.d("TensorflowHelper", "categories0: ${output[0].categories} ${categorySet}")

            /**
             * Find category by label
             */
            for (category in output[1].categories) {
                val categoryName = NoiseCategory.findCategoryByLabel(category.label)
                categorySet.add(categoryName)
            }
            Log.d("TensorflowHelper", "categories1: ${output[1].categories} ${categorySet}")

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

        val sortedMap = resultHashMap
            .toList()
            .sortedByDescending { (_, value) -> value }
            .toMap()

        Log.d("TensorflowHelper", "SortedMap: ${sortedMap}")

        emit(sortedMap)
    }.flowOn(Dispatchers.IO)
}

object NoiseCategory {

    fun findCategoryByIndex(index: Int): NoiseCategoryEnum {
        NoiseCategoryEnum.values().forEach { noiseCategoryEnum ->
            if (noiseCategoryEnum.indexArray.contains(index)) {
                return noiseCategoryEnum
            }
        }
        Log.e("NoiseCategory", "Unknown index: $index")
        return NoiseCategoryEnum.ETC
    }

    fun findCategoryByLabel(label: String): NoiseCategoryEnum {
        NoiseCategoryEnum.values().forEach { noiseCategoryEnum ->
            if (noiseCategoryEnum.labelArray?.contains(label) == true) {
                return noiseCategoryEnum
            }
        }
        Log.e("NoiseCategory", "Unknown label: $label")
        return NoiseCategoryEnum.ETC
    }

    enum class NoiseCategoryEnum(
        val label: String,
        val indexArray: Array<Int>,
        val labelArray: Array<String>? = null
    ) {
        VOCAL("Vocal", VocalList),
        HUMAN("Human", HumanList),
        FOOT_STEP("Footstep", FootStepList),
        CLASHING("Clashing", ClashingList),
        PET("Pet", PetList),
        ANIMAL("Animal", AnimalList),
        INSTRUMENT("Instrument", InstrumentList),
        NATURE("Nature", NatureList),
        VEHICLE("Vehicle", VehicleList, VehicleLabelList),
        MACHINE("Machine", MachineList, MachineLabelList),
        FURNITURE("Furniture", FurnitureList, FurnitureLabelList),
        LIVING("Living", LivingList),
        DOMESTIC("Domestic", DomesticList),
        CLASHING_HARD("Clashing (hard)", ClashingHardList, ClashingHardLabelList),
        EXPLOSION("Explosion", ExplosionList),
        ETC("Etc", EtcList),
    }

//    private val FootStepLabelList = arrayOf(
//        "발소리",
//    )
//    private val PetLabelList = arrayOf(
//        "개",
//        "고양이",
//    )
//    private val InstrumentLabelList = arrayOf(
//        "악기",
//    )
    private val VehicleLabelList = arrayOf(
        "경적",
        "사이렌",
        "주행음",
        "비행기",
//        "헬리콥터",
        "기차",
//        "지하철",
    )
    private val MachineLabelList = arrayOf(
//        "청소기",
//        "세탁기",
        "절삭기",
        "송풍기",
//        "압축기",
        "발전기",
    )
    private val FurnitureLabelList = arrayOf(
        "가구소리",
    )
    private val ClashingHardLabelList = arrayOf(
//        "공구",
//        "항타기",
        "파쇄기",
        "콘크리트펌프",
    )

    private val VocalList = arrayOf(
        0,
        1,
        2,
        3,
        4,
        5,
        6,
        7,
        8,
        9,
        10,
        11,
        12,
        13,
        14,
        15,
        16,
        17,
        18,
        19,
        20,
        21,
        22,
        23,
        24,
        25,
        26,
        27,
        28,
        29,
        30,
        31,
        32,
        33,
        34,
        35,
        36,
        37,
        38,
        39,
        40,
        41,
        42,
        43,
        44,
        45,
        61,
        62,
        63,
        64,
        65,
        66,
        490,
        493,
        497,
        510,
        518,
        519,
    )
    private val HumanList = arrayOf(
        36,
        37,
        38,
        39,
        40,
        41,
        42,
        43,
        44,
        45,
        49,
        50,
        51,
        52,
        53,
        54,
        55,
        59,
        60,
        499,
    )
    private val FootStepList = arrayOf(
        46,
        47,
        48,
        120,
    )
    private val ClashingList = arrayOf(
        56,
        57,
        58,
        459,
        461,
        462,
        463,
        464,
        465,
        466,
        467,
        468,
        469,
        470,
        471,
        473,
        474,
        478,
    )
    private val PetList = arrayOf(
        67,
        68,
        69,
        70,
        71,
        72,
        73,
        74,
        75,
        76,
        77,
        78,
        79,
        80,
    )
    private val AnimalList = arrayOf(
        81,
        82,
        83,
        84,
        85,
        86,
        87,
        88,
        89,
        90,
        91,
        92,
        93,
        94,
        95,
        96,
        97,
        98,
        99,
        100,
        101,
        102,
        103,
        104,
        105,
        106,
        107,
        108,
        109,
        110,
        111,
        112,
        113,
        114,
        115,
        116,
        117,
        118,
        119,
        121,
        122,
        123,
        124,
        125,
        126,
        127,
        128,
        129,
        130,
        131,
    )
    private val InstrumentList = arrayOf(
        132,
        133,
        134,
        135,
        136,
        137,
        138,
        139,
        140,
        141,
        142,
        143,
        144,
        145,
        146,
        147,
        148,
        149,
        150,
        151,
        152,
        153,
        154,
        155,
        156,
        157,
        158,
        159,
        160,
        161,
        162,
        163,
        164,
        165,
        166,
        167,
        168,
        169,
        170,
        171,
        172,
        173,
        174,
        175,
        176,
        177,
        178,
        179,
        180,
        181,
        182,
        183,
        184,
        185,
        186,
        187,
        188,
        189,
        190,
        191,
        192,
        193,
        194,
        195,
        196,
        197,
        198,
        199,
        200,
        201,
        202,
        203,
        204,
        205,
        206,
        207,
        208,
        209,
        210,
        211,
        212,
        213,
        214,
        215,
        216,
        217,
        218,
        219,
        220,
        221,
        222,
        223,
        224,
        225,
        226,
        227,
        228,
        229,
        230,
        231,
        232,
        233,
        234,
        235,
        236,
        237,
        238,
        239,
        240,
        241,
        242,
        243,
        244,
        245,
        246,
        247,
        248,
        249,
        250,
        251,
        252,
        253,
        254,
        255,
        256,
        257,
        258,
        259,
        260,
        261,
        262,
        263,
        264,
        265,
        266,
        267,
        268,
        269,
        270,
        271,
        272,
        273,
        274,
        275,
        276,
        457,
        458,
        498,
    )
    private val NatureList = arrayOf(
        277,
        278,
        279,
        280,
        281,
        282,
        283,
        284,
        285,
        286,
        287,
        288,
        289,
        290,
        291,
        292,
        293,
        508,
    )
    private val VehicleList = arrayOf(
        294,
        295,
        296,
        297,
        298,
        299,
        300,
        301,
        302,
        303,
        304,
        305,
        306,
        307,
        308,
        309,
        310,
        311,
        312,
        313,
        314,
        315,
        316,
        317,
        318,
        319,
        320,
        321,
        322,
        323,
        324,
        325,
        326,
        327,
        328,
        329,
        330,
        331,
        332,
        333,
        334,
        335,
        336,
        337,
        338,
        344,
        345,
        346,
        347,
    )
    private val MachineList = arrayOf(
        340,
        341,
        342,
        343,
    )
    private val FurnitureList = arrayOf(
        348,
        349,
        350,
        351,
        352,
        353,
        354,
        355,
        356,
        357,
        358,
        479,
        480,
    )
    private val LivingList = arrayOf(
        359,
        360,
        361,
        364,
        365,
        366,
        368,
        369,
        372,
        373,
        374,
        375,
        377,
        378,
        379,
        380,
        381,
        382,
        383,
        384,
        385,
        386,
        387,
        388,
        389,
        390,
        391,
        392,
        393,
        394,
        395,
        396,
        397,
        399,
        400,
        401,
        402,
        410,
        411,
        415,
        416,
        435,
        436,
        437,
        438,
        439,
        440,
        441,
        442,
        443,
        444,
        445,
        446,
        447,
        448,
        449,
        450,
        453,
        475,
        476,
        477,
        481,
        482,
        483,
        484,
        485,
        486,
        488,
        489,
    )
    private val DomesticList = arrayOf(
        362,
        363,
        367,
        370,
        371,
        376,
        398,
        405,
        406,
        407,
        408,
        409,
        456,
        339,
    )
    private val ClashingHardList = arrayOf(
        403,
        404,
        412,
        413,
        414,
        417,
        418,
        419,
        432,
        433,
        434,
        454,
        455,
        472,
    )
    private val ExplosionList = arrayOf(
        420,
        421,
        422,
        423,
        424,
        425,
        426,
        427,
        428,
        429,
        430,
        460,
    )
    private val EtcList = arrayOf(
        451,
        452,
        487,
        491,
        492,
        494,
        495,
        431,
        500,
        501,
        502,
        503,
        504,
        505,
        506,
        507,
        509,
        511,
        512,
        513,
        514,
        515,
        516,
        517,
        520,
    )
}