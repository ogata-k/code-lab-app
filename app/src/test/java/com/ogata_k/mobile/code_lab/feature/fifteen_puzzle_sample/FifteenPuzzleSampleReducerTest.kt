package com.ogata_k.mobile.code_lab.feature.fifteen_puzzle_sample

import com.ogata_k.mobile.code_lab.domain.`class`.FifteenPuzzleBoard
import com.ogata_k.mobile.code_lab.domain.enum.FifteenPuzzleDifficulty
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * FifteenPuzzleSampleReducerのテスト
 */
class FifteenPuzzleSampleReducerTest {
    private val reducer = FifteenPuzzleSampleReducer()

    @Test
    fun `UpdateGridSizeSettingミューテーションによってgridSizeが更新されること`() {
        val initialState = FifteenPuzzleSampleUiState.NotStart(gridSize = 4u)
        val mutation = FifteenPuzzleSampleMutation.UpdateGridSizeSetting(3u)

        val newState = reducer.reduce(initialState, mutation)

        assertEquals(3u, (newState as FifteenPuzzleSampleUiState.NotStart).gridSize)
    }

    @Test
    fun `UpdateDifficultySettingミューテーションによってdifficultyが更新されること`() {
        val initialState =
            FifteenPuzzleSampleUiState.NotStart(difficulty = FifteenPuzzleDifficulty.Normal)
        val mutation =
            FifteenPuzzleSampleMutation.UpdateDifficultySetting(FifteenPuzzleDifficulty.Hard)

        val newState = reducer.reduce(initialState, mutation)

        assertEquals(
            FifteenPuzzleDifficulty.Hard,
            (newState as FifteenPuzzleSampleUiState.NotStart).difficulty
        )
    }

    @Test
    fun `SetBoardAndStartPlayミューテーションによってPlaying状態に遷移すること`() {
        val initialState = FifteenPuzzleSampleUiState.NotStart()
        val board = mockk<FifteenPuzzleBoard>()
        val mutation = FifteenPuzzleSampleMutation.SetBoardAndStartPlay(board, 50u)

        val newState = reducer.reduce(initialState, mutation)

        val playingState = newState as FifteenPuzzleSampleUiState.Playing
        assertEquals(board, playingState.board)
        assertEquals(50u, playingState.estimateBoardDifficulty)
        assertEquals(0u, playingState.stepCount)
    }
}
