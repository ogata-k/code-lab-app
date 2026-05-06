package com.ogata_k.mobile.code_lab.feature.select_template

import com.ogata_k.mobile.code_lab.common.logI
import com.ogata_k.mobile.code_lab.core.mvi.ActionProcessor
import com.ogata_k.mobile.code_lab.core.mvi.CommonMutation
import com.ogata_k.mobile.code_lab.core.mvi.CommonUiEffect
import com.ogata_k.mobile.code_lab.core.mvi.StoreScope
import com.ogata_k.mobile.code_lab.ui.widget.dialog.CommonDialogData
import com.ogata_k.mobile.code_lab.ui.widget.dialog.CommonDialogMessage
import com.ogata_k.mobile.code_lab.ui.widget.snackbar.CommonSnackbarData
import com.ogata_k.mobile.code_lab.ui.widget.snackbar.CommonSnackbarMessage
import kotlinx.coroutines.delay
import javax.inject.Inject

/**
 * SelectTemplate featureのアクションを処理し、ミューテーションを生成するクラス
 */
class SelectTemplateActionProcessor @Inject constructor() :
    ActionProcessor<SelectTemplateUiState, SelectTemplateUiEffect, SelectTemplateIntent, SelectTemplateAction, SelectTemplateMutation> {
    override suspend fun process(
        action: SelectTemplateAction,
        scope: StoreScope<SelectTemplateUiState, SelectTemplateUiEffect, SelectTemplateIntent, SelectTemplateAction, SelectTemplateMutation>
    ) {
        when (action) {
            is SelectTemplateAction.Initialize -> {
                // ローディングを表示
                val loading = CommonDialogData.ShowLoading()
                scope.emitCommonMutation(CommonMutation.AddDialog(loading))

                delay(1000)
                scope.emitMutation(SelectTemplateMutation.ToInitialized)

                // 共通のSide Effectを使ってスナックバーを表示
                scope.emitCommonUiEffect(
                    CommonUiEffect.ShowSnackbar(
                        CommonSnackbarData(message = CommonSnackbarMessage.Initialized)
                    )
                )

                // ローディングを完了ダイアログに置き換える
                scope.emitCommonMutation(
                    CommonMutation.ReplaceDialog(
                        data = CommonDialogData.ShowConfirmDialog(
                            message = CommonDialogMessage.Initialized,
                            onDismiss = {
                                logI("CommonMutation") { "confirm dialog on dismiss" }
                            }
                        ),
                        fromData = loading
                    )
                )
            }
        }
    }
}
