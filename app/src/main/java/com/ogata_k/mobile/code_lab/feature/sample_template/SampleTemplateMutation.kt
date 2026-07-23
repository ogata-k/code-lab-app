package com.ogata_k.mobile.code_lab.feature.sample_template

import com.ogata_k.mobile.code_lab.core.mvi.Mutation

/**
 * SampleTemplate featureの状態を変更するための変更内容
 */
sealed interface SampleTemplateMutation : Mutation {
    // TODO: 本来のMutationに書き換える
    data object ToInitialized : SampleTemplateMutation
}