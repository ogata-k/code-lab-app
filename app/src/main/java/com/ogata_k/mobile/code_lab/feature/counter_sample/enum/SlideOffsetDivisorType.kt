package com.ogata_k.mobile.code_lab.feature.counter_sample.enum

enum class SlideOffsetDivisorType {
    // 全高
    Full,

    // 半分
    Half,

    // 控え目
    Subtle;

    fun toOffsetYDivisor(): Int {
        return when (this) {
            Full -> 1
            Half -> 2
            Subtle -> 4
        }
    }
}
