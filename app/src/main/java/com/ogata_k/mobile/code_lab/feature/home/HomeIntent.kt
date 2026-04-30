package com.ogata_k.mobile.code_lab.feature.home

import com.ogata_k.mobile.code_lab.core.mvi.Intent

/**
 * Home に対するユーザーの意図（操作）
 */
sealed interface HomeIntent : Intent<HomeAction>