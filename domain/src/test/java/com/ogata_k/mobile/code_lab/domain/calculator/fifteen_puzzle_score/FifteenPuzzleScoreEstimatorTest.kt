package com.ogata_k.mobile.code_lab.domain.calculator.fifteen_puzzle_score

import com.ogata_k.mobile.code_lab.domain.enum.FifteenPuzzleDifficulty
import org.junit.Assert.assertTrue
import org.junit.Test

class FifteenPuzzleScoreEstimatorTest {

    /**
     * 同じグリッドサイズにおいて、難易度が上がるにつれて推定スコアが単調増加することを確認する
     * 対象：3x3 から 10x10 まで
     */
    @Test
    fun estimateMaxScore_increasesWithDifficulty() {
        val calculator = StandardScoreCalculator()
        val gridSizes = (3u..10u).toList()
        val difficulties = FifteenPuzzleDifficulty.entries

        for (size in gridSizes) {
            var previousScore = 0u
            for (difficulty in difficulties) {
                val currentScore =
                    FifteenPuzzleScoreEstimator.estimateMaxScore(calculator, size, difficulty)

                assertTrue(
                    "Size $size: Score should increase with difficulty. $difficulty ($currentScore) should be > previous score ($previousScore)",
                    currentScore > previousScore
                )
                previousScore = currentScore
            }
        }
    }

    /**
     * 同じ難易度において、グリッドサイズが大きくなるにつれて推定スコアが単調増加することを確認する
     * 対象：3x3 から 10x10 まで
     */
    @Test
    fun estimateMaxScore_increasesWithGridSize() {
        val calculator = StandardScoreCalculator()
        val gridSizes = (3u..10u).toList()
        val difficulties = FifteenPuzzleDifficulty.entries

        for (difficulty in difficulties) {
            var previousScore = 0u
            for (size in gridSizes) {
                val currentScore =
                    FifteenPuzzleScoreEstimator.estimateMaxScore(calculator, size, difficulty)

                assertTrue(
                    "Difficulty $difficulty: Score should increase with size. Size $size ($currentScore) should be > previous score ($previousScore)",
                    currentScore > previousScore
                )
                previousScore = currentScore
            }
        }
    }
}
