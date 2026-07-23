package com.ogata_k.mobile.code_lab.feature.select_template

import com.ogata_k.mobile.code_lab.core.mvi.Mutation

/**
 * SelectTemplate featureの状態を変更するための変更内容
 */
sealed interface SelectTemplateMutation : Mutation {
    data object ToInitialized : SelectTemplateMutation
}