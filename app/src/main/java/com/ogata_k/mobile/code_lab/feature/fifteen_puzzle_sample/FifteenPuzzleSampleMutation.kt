package com.ogata_k.mobile.code_lab.feature.fifteen_puzzle_sample

import com.ogata_k.mobile.code_lab.core.mvi.Mutation
import com.ogata_k.mobile.code_lab.domain.`class`.FifteenPuzzleBoard
import com.ogata_k.mobile.code_lab.domain.enum.FifteenPuzzleDifficulty

/**
 * FifteenPuzzleSample featureの状態を変更するための変更内容
 */
sealed interface FifteenPuzzleSampleMutation : Mutation {
    data class UpdateGridSizeSetting(val gridSize: UInt) : FifteenPuzzleSampleMutation
    data class UpdateDifficultySetting(val difficulty: FifteenPuzzleDifficulty) :
        FifteenPuzzleSampleMutation

    data class SetBoardAndStartPlay(
        val board: FifteenPuzzleBoard,
        val estimateBoardDifficulty: UInt
    ) : FifteenPuzzleSampleMutation

    data class IncrementBoardState(val board: FifteenPuzzleBoard) : FifteenPuzzleSampleMutation
}