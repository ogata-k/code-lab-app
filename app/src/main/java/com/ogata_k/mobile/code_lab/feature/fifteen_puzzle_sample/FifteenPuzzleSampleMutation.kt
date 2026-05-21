package com.ogata_k.mobile.code_lab.feature.fifteen_puzzle_sample

import com.ogata_k.mobile.code_lab.core.mvi.Mutation

/**
 * FifteenPuzzleSample featureの状態を変更するための変更内容
 */
sealed interface FifteenPuzzleSampleMutation : Mutation {
    // TODO: 本来のMutationに書き換える
    data object ToInitialized : FifteenPuzzleSampleMutation
}