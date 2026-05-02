package com.ogata_k.mobile.code_lab.feature.home

import com.ogata_k.mobile.code_lab.core.mvi.Mutation

/**
 * Home featureの状態を変更するための変更内容
 */
sealed interface HomeMutation : Mutation {
    data object ToInitialized : HomeMutation
}