package com.greenie.app.feature.deepmediresult.constant

internal enum class HealthMaxValue(val normal: Int, val warning: Int, val caution: Int, val danger: Int) {
    BPM(80, 100, 150, 200),
    SYS(120, 140, 160, 200),
    DIA(70, 90, 110, 120),
    RESPIRATORY_RATE(8, 12, 16, 20),
    FATIGUE(80, 100, 150, 200),
    STRESS(1, 2, 4, 5),
}
