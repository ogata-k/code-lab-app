package com.ogata_k.mobile.code_lab.feature.fifteen_puzzle_sample

import com.ogata_k.mobile.code_lab.core.mvi.Intent

/**
 * FifteenPuzzleSample featureに対するユーザーの意図（操作）
 */
sealed interface FifteenPuzzleSampleIntent : Intent<FifteenPuzzleSampleAction> {
    override fun toAction(): FifteenPuzzleSampleAction? = when (this) {
        // TODO: Intentが増えたらここに追加
        else -> null
    }
}