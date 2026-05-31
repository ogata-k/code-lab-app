package com.ogata_k.mobile.code_lab.domain.calculator.fifteen_puzzle_score

import com.ogata_k.mobile.code_lab.domain.enum.FifteenPuzzleDifficulty
import kotlin.math.roundToInt

class StandardCalculator : ScoreCalculator {
    override fun calculate(
        gridSize: UInt,
        difficulty: FifteenPuzzleDifficulty,
        estimateDifficultyValue: UInt,
        stepCount: UInt
    ): UInt {
        if (stepCount == 0u) return 0u
        val adjustScaler = 2.5
        val n = gridSize.toDouble()
        val sizeScaler: Double = when (gridSize.toInt()) {
            in 0..3 -> 0.3
            4 -> 1.0   // 4x4: 面積 16 (基準)
            5 -> 2.5   // 5x5: 面積 25
            6 -> 5.7   // 6x6: 面積 36
            else -> (n * n * n / 64.0) * (1.0 + (n - 4) * 0.25)
        }
        return (adjustScaler * estimateDifficultyValue.toDouble() * (estimateDifficultyValue.toInt() + 40) * sizeScaler / stepCount.toInt()).roundToInt()
            .coerceIn(0, Int.MAX_VALUE).toUInt()
    }
}
