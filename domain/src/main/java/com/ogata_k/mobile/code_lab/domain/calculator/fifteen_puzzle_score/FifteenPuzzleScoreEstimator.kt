package com.ogata_k.mobile.code_lab.domain.calculator.fifteen_puzzle_score

import com.ogata_k.mobile.code_lab.domain.enum.FifteenPuzzleDifficulty
import kotlin.math.roundToInt

/**
 * 盤面のサイズと難易度から、理論上の最大獲得スコアを推定するクラス
 */
object FifteenPuzzleScoreEstimator {
    /**
     * 指定された条件での推定最大スコアを算出する
     */
    fun estimateMaxScore(
        calculator: ScoreCalculator,
        gridSize: UInt,
        difficulty: FifteenPuzzleDifficulty
    ): UInt {
        val edv = difficulty.difficultyAverage
        val idealStepCount = estimateIdealStepCount(gridSize, difficulty)

        return calculator.calculate(
            gridSize = gridSize,
            difficulty = difficulty,
            estimateDifficultyValue = edv,
            stepCount = idealStepCount
        )
    }

    /**
     * 指定された条件での推定最大スコアを算出するときに利用する想定手数を求める
     */
    fun estimateIdealStepCount(
        gridSize: UInt,
        difficulty: FifteenPuzzleDifficulty
    ): UInt {
        val edv = difficulty.difficultyAverage
        val n = gridSize.toDouble()

        // 正規化係数を用いて、理想的な手数(rawValueの期待値)を逆算する
        val maxEstimatedValue = n * n * n * 0.7
        val idealStepCount = (edv.toDouble() / 100.0 * maxEstimatedValue)
            .roundToInt()

        return idealStepCount.coerceAtLeast(1).toUInt()
    }
}
