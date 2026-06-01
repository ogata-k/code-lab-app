package com.ogata_k.mobile.code_lab.domain.calculator.fifteen_puzzle_score

import com.ogata_k.mobile.code_lab.domain.enum.FifteenPuzzleDifficulty
import kotlin.math.pow
import kotlin.math.roundToInt

class StandardCalculator : ScoreCalculator {
    companion object {
        private const val ADJUST_SCALER = 5.0
        private const val SIZE_EXPONENT = 3.75
    }

    override fun calculate(
        gridSize: UInt,
        difficulty: FifteenPuzzleDifficulty,
        estimateDifficultyValue: UInt,
        stepCount: UInt
    ): UInt {
        if (stepCount == 0u) return 0u
        val e = estimateDifficultyValue.toDouble()
        val n = gridSize.toDouble()
        val steps = stepCount.toDouble()

        val difficultyScore = e * (e + 40.0)

        val sizeScaler = (n / 4.0).pow(SIZE_EXPONENT)

        val score = ADJUST_SCALER *
                difficultyScore *
                sizeScaler /
                steps

        return score
            .roundToInt()
            .coerceAtLeast(0)
            .toUInt()
    }
}
