package com.ogata_k.mobile.code_lab.domain.`class`

import com.ogata_k.mobile.code_lab.domain.enum.FifteenPuzzleDifficulty
import kotlin.math.abs

@ConsistentCopyVisibility
data class FifteenPuzzleBoard private constructor(
    val gridSize: UInt,
    val difficulty: FifteenPuzzleDifficulty,
    val values: List<UInt> = List((gridSize * gridSize).toInt()) { (it + 1).toUInt() },
) {
    companion object {
        /**
         * 盤面をシャッフルして難易度と一緒に返す
         */
        fun generateBoardForDifficulty(
            gridSize: UInt,
            difficulty: FifteenPuzzleDifficulty,
            timeoutMillis: Long = 2500L
        ): Pair<FifteenPuzzleBoard, UInt> {
            if (gridSize <= 1u) {
                return Pair(FifteenPuzzleBoard(gridSize, difficulty), 0u)
            }

            val range = difficulty.difficultyRange
            val targetAverage = difficulty.difficultyAverage
            val startTime = System.currentTimeMillis()

            var bestBoard = FifteenPuzzleBoard(gridSize, difficulty)
            var bestDifficultyValue = FifteenPuzzleDifficulty.estimateDifficultyValue(bestBoard)
            var bestDiff = Int.MAX_VALUE

            val candidates = mutableListOf<Pair<FifteenPuzzleBoard, UInt>>()

            var iteration = 0
            // タイムアウトまで探索を繰り返す
            while (System.currentTimeMillis() - startTime < timeoutMillis) {
                // 偶数回はランダム（高難易度側）から、奇数回はゴール（低難易度側）から開始
                // これにより、範囲の上限付近と下限付近の両方の盤面を拾いやすくし、分布を一様化する。
                var currentBoard = if (iteration % 2 == 0) {
                    generateRandomSolvableBoard(gridSize, difficulty)
                } else {
                    FifteenPuzzleBoard(gridSize, difficulty)
                }
                iteration++

                var lastEmptyIndex: Int? = null
                var stagnationCount = 0

                // 1つの開始点から一定歩数（または停滞するまで）山登り探索を行う
                for (step in 0 until 200) {
                    if (System.currentTimeMillis() - startTime >= timeoutMillis) break

                    val currentDifficulty =
                        FifteenPuzzleDifficulty.estimateDifficultyValue(currentBoard)

                    // 範囲内の盤面を候補として蓄積
                    if (!currentBoard.isGoal() && currentDifficulty >= range.first && currentDifficulty < range.second) {
                        candidates.add(Pair(currentBoard, currentDifficulty))
                    }

                    // 最良のものを保持（候補が足りなかった時のバックアップ）
                    val currentDiff = abs(currentDifficulty.toInt() - targetAverage.toInt())
                    if (currentDiff < bestDiff) {
                        bestDiff = currentDiff
                        bestBoard = currentBoard
                        bestDifficultyValue = currentDifficulty
                        stagnationCount = 0
                    } else {
                        stagnationCount++
                    }

                    // 周囲の盤面から最も目標に近いものを選ぶ（Greedy Search）
                    val nextMoves = currentBoard.getPossibleMoves(lastEmptyIndex)
                    if (nextMoves.isEmpty()) break

                    // 1/10の確率でランダム移動（局所解からの脱出）、それ以外は最良移動
                    val nextMove = if ((0..9).random() == 0) {
                        nextMoves.random()
                    } else {
                        nextMoves.minBy { move ->
                            abs(
                                FifteenPuzzleDifficulty.estimateDifficultyValue(move.first)
                                    .toInt() - targetAverage.toInt()
                            )
                        }
                    }

                    currentBoard = nextMove.first
                    lastEmptyIndex = nextMove.second
                }

                // 偶数回（高難易度アプローチと低難易度アプローチの1セット）が完了し、
                // かつ十分な候補が集まっていれば終了。
                // これにより、下からと 上からの盤面がバランスよく蓄積される。
                if (iteration % 2 == 0 && candidates.size >= 40) {
                    return candidates.random()
                }
            }

            return if (candidates.isNotEmpty()) {
                candidates.random()
            } else {
                Pair(bestBoard, bestDifficultyValue)
            }
        }

        /**
         * 指定された難易度向けにランダムな可解盤面を生成する
         */
        private fun generateRandomSolvableBoard(
            gridSize: UInt,
            difficulty: FifteenPuzzleDifficulty
        ): FifteenPuzzleBoard {
            val size = (gridSize * gridSize).toInt()
            val values = (1..size).map { it.toUInt() }.toMutableList()

            while (true) {
                values.shuffle()
                if (isSolvable(gridSize.toInt(), values)) {
                    return FifteenPuzzleBoard(gridSize, difficulty, values)
                }
            }
        }

        /**
         * 盤面が解法可能かどうかを判定する
         */
        private fun isSolvable(gridSize: Int, values: List<UInt>): Boolean {
            val emptyValue = (gridSize * gridSize).toUInt()
            var inversions = 0
            val flatValues = values.filter { it != emptyValue }

            for (i in 0 until flatValues.size) {
                for (j in i + 1 until flatValues.size) {
                    if (flatValues[i] > flatValues[j]) {
                        inversions++
                    }
                }
            }

            return if (gridSize % 2 != 0) {
                inversions % 2 == 0
            } else {
                val emptyIndex = values.indexOf(emptyValue)
                val emptyRowFromBottom = gridSize - (emptyIndex / gridSize)
                (emptyRowFromBottom + inversions) % 2 != 0
            }
        }
    }

    /**
     * 移動可能な全ての隣接盤面を取得する
     */
    private fun getPossibleMoves(excludeIndex: Int?): List<Pair<FifteenPuzzleBoard, Int>> {
        val size = gridSize.toInt()
        val emptyIndex = values.indexOf(gridSize * gridSize)
        val emptyRow = emptyIndex / size
        val emptyCol = emptyIndex % size

        val moves = mutableListOf<Int>()
        if (emptyRow > 0) moves.add(emptyIndex - size)
        if (emptyRow < size - 1) moves.add(emptyIndex + size)
        if (emptyCol > 0) moves.add(emptyIndex - 1)
        if (emptyCol < size - 1) moves.add(emptyIndex + 1)

        return moves.filter { it != excludeIndex }.map { targetIndex ->
            val newValues = values.toMutableList()
            newValues[emptyIndex] = values[targetIndex]
            newValues[targetIndex] = values[emptyIndex]
            Pair(copy(values = newValues), emptyIndex)
        }
    }

    /**
     * 指定したタイルの値を空きマスと入れ替える。
     */
    fun move(value: UInt): FifteenPuzzleBoard? {
        val size = gridSize.toInt()
        val emptyIndex = values.indexOf(gridSize * gridSize)
        val targetIndex = values.indexOf(value)
        if (targetIndex == -1) return null

        val isAdjacent = abs(emptyIndex / size - targetIndex / size) +
                abs(emptyIndex % size - targetIndex % size) == 1
        if (!isAdjacent) return null

        val newValues = values.toMutableList()
        newValues[emptyIndex] = values[targetIndex]
        newValues[targetIndex] = values[emptyIndex]
        return copy(values = newValues)
    }

    /**
     * 全てのタイルが正しい位置にあるか
     */
    fun isGoal(): Boolean = values.indices.all { index -> values[index] == (index + 1).toUInt() }

    /**
     * 空の値を表す値
     */
    val emptyValue: UInt get() = gridSize * gridSize
}
