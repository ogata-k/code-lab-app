package com.ogata_k.mobile.code_lab.domain.calculator.fifteen_puzzle_score

import com.ogata_k.mobile.code_lab.domain.enum.FifteenPuzzleDifficulty

interface ScoreCalculator {
    fun calculate(
        gridSize: UInt,
        difficulty: FifteenPuzzleDifficulty,
        estimateDifficultyValue: UInt,
        stepCount: UInt
    ): UInt
}