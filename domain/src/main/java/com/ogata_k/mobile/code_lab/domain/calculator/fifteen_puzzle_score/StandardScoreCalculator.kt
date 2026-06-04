package com.ogata_k.mobile.code_lab.domain.calculator.fifteen_puzzle_score

import com.ogata_k.mobile.code_lab.domain.enum.FifteenPuzzleDifficulty
import kotlin.math.pow
import kotlin.math.roundToInt

class StandardScoreCalculator : ScoreCalculator {
    companion object {
        private const val ADJUST_SCALER = 0.8
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

        val sizeScaler = (n / 3.0).pow(4.0 + e / 200.0)

        val adjustedSteps = (steps / n).pow(0.7)

        val score = ADJUST_SCALER *
                difficultyScore *
                sizeScaler /
                (adjustedSteps + 1.0)

        return score
            .roundToInt()
            .coerceAtLeast(0)
            .toUInt()
    }
}
