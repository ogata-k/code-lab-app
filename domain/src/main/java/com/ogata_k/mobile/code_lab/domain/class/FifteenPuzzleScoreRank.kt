package com.ogata_k.mobile.code_lab.domain.`class`

enum class FifteenPuzzleScoreRank {
    F,
    E,
    D,
    C,
    B,
    A,
    S,
    SS,
    SSS;

    companion object {
        fun fromScore(score: UInt): FifteenPuzzleScoreRank {
            return when (score) {
                in 0u..29u -> F
                in 30u..59u -> E
                in 60u..99u -> D
                in 100u..149u -> C
                in 150u..219u -> B
                in 220u..299u -> A
                in 300u..399u -> S
                in 400u..549u -> SS
                else -> SSS
            }
        }
    }
}