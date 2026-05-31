package com.ogata_k.mobile.code_lab.domain.enum

import com.ogata_k.mobile.code_lab.common.BuildConfig
import com.ogata_k.mobile.code_lab.domain.`class`.FifteenPuzzleBoard
import kotlin.math.abs

/**
 * 15パズルの難易度
 */
enum class FifteenPuzzleDifficulty {
    Easy,
    Normal,
    Hard,
    Extreme,

    Nightmare;

    companion object {
        fun fromDifficultyValue(value: UInt): FifteenPuzzleDifficulty {
            return when (value) {
                in Easy.difficultyRange.first..<Easy.difficultyRange.second -> Easy
                in Normal.difficultyRange.first..<Normal.difficultyRange.second -> Normal
                in Hard.difficultyRange.first..<Hard.difficultyRange.second -> Hard
                in Extreme.difficultyRange.first..<Extreme.difficultyRange.second -> Extreme
                in Nightmare.difficultyRange.first..Nightmare.difficultyRange.second -> Nightmare
                else -> error("Unreachable difficulty value: $value")
            }
        }

        /**
         * 盤面から難易度を計算して推定難易度を返す
         */
        fun estimateDifficultyValue(board: FifteenPuzzleBoard): UInt {
            val gridSize = board.gridSize
            val boardValues = board.values
            if (BuildConfig.DEBUG) {
                require(gridSize * gridSize == boardValues.size.toUInt())
            }

            val mDist: UInt = manhattanDistSum(gridSize, boardValues)
            val lc: UInt = linearConflict(gridSize, boardValues)
            val rawValue = mDist + 2u * lc

            // --- 0-100 への正規化 ---
            // 最大難易度を100の目安として、どれくらい近いかで正規化
            val n3 = (gridSize * gridSize * gridSize).toDouble()
            val maxEstimatedValue = n3 * 0.7

            val normalized = (rawValue.toDouble() / maxEstimatedValue * 100.0)
                .toInt()
                .coerceIn(0, 100)

            return normalized.toUInt()
        }

        /**
         * ゴール盤面からのセル位置をマンハッタン距離で計算して合計を返す
         */
        private fun manhattanDistSum(gridSize: UInt, boardValues: List<UInt>): UInt {
            val gridSizeInt: Int = gridSize.toInt()
            val emptyValue = gridSize * gridSize

            val distSum: Int = boardValues.foldIndexed(0) { index, currentDistSum, value ->
                if (value == emptyValue) {
                    // 空白セルはスキップ
                    currentDistSum
                } else {
                    val row: Int = index / gridSizeInt
                    val col: Int = index % gridSizeInt

                    val targetIndex = (value - 1u).toInt()
                    val goalRow: Int = targetIndex / gridSizeInt
                    val goalCol: Int = targetIndex % gridSizeInt

                    currentDistSum + abs(row - goalRow) + abs(col - goalCol)
                }
            }

            return distSum.toUInt()
        }

        /**
         * 線形衝突（Linear Conflict）を計算する。
         * つまり、同一行・列上で目標位置が逆転しているタイルのペアを探す。
         */
        private fun linearConflict(gridSize: UInt, boardValues: List<UInt>): UInt {
            val gridSizeInt: Int = gridSize.toInt()
            val emptyValue = gridSize * gridSize

            var conflicts = 0u

            // row conflicts
            for (row in 0 until gridSizeInt) {
                for (c1 in 0 until gridSizeInt) {
                    val v1 = boardValues[row * gridSizeInt + c1]
                    // 空白セルはスキップ
                    if (v1 == emptyValue) continue

                    val v1Index = (v1 - 1u).toInt()
                    val goalV1Row = v1Index / gridSizeInt
                    val goalV1Col = v1Index % gridSizeInt

                    // 同じ行じゃないなら計算対象外
                    if (goalV1Row != row) continue

                    for (c2 in c1 + 1 until gridSizeInt) {
                        val v2 = boardValues[row * gridSizeInt + c2]
                        // 空白セルはスキップ
                        if (v2 == emptyValue) continue

                        val v2Index = (v2 - 1u).toInt()
                        val goalV2Row = v2Index / gridSizeInt
                        val goalV2Col = v2Index % gridSizeInt

                        // 同じ行じゃないなら計算対象外
                        if (goalV2Row != row) continue

                        // 列が入れ替わっているならカウント
                        if (goalV1Col > goalV2Col) {
                            conflicts++
                        }
                    }
                }
            }

            // col conflicts
            for (col in 0 until gridSizeInt) {
                for (r1 in 0 until gridSizeInt) {
                    val v1 = boardValues[r1 * gridSizeInt + col]
                    // 空白セルはスキップ
                    if (v1 == emptyValue) continue

                    val v1Index = (v1 - 1u).toInt()
                    val goalV1Row = v1Index / gridSizeInt
                    val goalV1Col = v1Index % gridSizeInt

                    // 同じ列じゃないなら計算対象外
                    if (goalV1Col != col) continue

                    for (r2 in r1 + 1 until gridSizeInt) {
                        val v2 = boardValues[r2 * gridSizeInt + col]
                        // 空白セルはスキップ
                        if (v2 == emptyValue) continue

                        val v2Index = (v2 - 1u).toInt()
                        val goalV2Row = v2Index / gridSizeInt
                        val goalV2Col = v2Index % gridSizeInt

                        // 同じ列じゃないなら計算対象外
                        if (goalV2Col != col) continue

                        // 行が入れ替わっているならカウント
                        if (goalV1Row > goalV2Row) {
                            conflicts++
                        }
                    }
                }
            }

            return conflicts
        }
    }

    /**
     * Pair(A, B)でA以上B未満を表し、この範囲を該当の難易度として扱う。
     */
    val difficultyRange: Pair<UInt, UInt>
        get() = when (this) {
            Easy -> Pair(UInt.MIN_VALUE, 20u)
            Normal -> Pair(20u, 40u)
            Hard -> Pair(40u, 60u)
            Extreme -> Pair(60u, 80u)
            Nightmare -> Pair(80u, UInt.MAX_VALUE)
        }

    /**
     * 盤面の一般的な難易度値
     */
    val difficultyAverage: UInt
        get() = when (this) {
            Easy -> 10u
            Normal -> 30u
            Hard -> 50u
            Extreme -> 70u
            Nightmare -> 90u
        }
}