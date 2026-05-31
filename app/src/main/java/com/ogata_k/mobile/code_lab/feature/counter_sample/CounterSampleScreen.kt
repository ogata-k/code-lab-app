package com.ogata_k.mobile.code_lab.feature.counter_sample

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.ogata_k.mobile.code_lab.R
import com.ogata_k.mobile.code_lab.feature.counter_sample.enum.SlideOffsetDivisorType
import com.ogata_k.mobile.code_lab.ui.theme.FontSizeM
import com.ogata_k.mobile.code_lab.ui.theme.FontSizeXXXXL
import com.ogata_k.mobile.code_lab.ui.theme.SpacingM
import com.ogata_k.mobile.code_lab.ui.theme.SpacingS
import com.ogata_k.mobile.code_lab.ui.theme.SpacingXS
import com.ogata_k.mobile.code_lab.ui.theme.SpacingXXXXL
import com.ogata_k.mobile.code_lab.ui.widget.screen.BasicScaffold
import com.ogata_k.mobile.code_lab.ui.widget.text.LabelMediumText
import com.ogata_k.mobile.code_lab.ui.widget.text.TitleSmallText

/**
 * CounterSample featureのメイン画面を表示するComposable関数
 */
@Composable
fun CounterSampleScreen(
    uiState: CounterSampleUiState,
    onIntent: (CounterSampleIntent) -> Unit,
    onBack: (() -> Unit)?,
) {
    BasicScaffold(
        title = stringResource(R.string.screen_title_counter),
        onBack = onBack,
    ) {
        // 下記アニメーションサンプルを利用したサンプル
        // ref: https://github.com/skydoves/compose-animations/blob/main/app/src/main/kotlin/com/skydoves/hotreloadanimations/animations/AnimationExample5.kt

        // スライドインしてくるアニメーション速度（Ms）
        val slideAnimationDurationMs = rememberAndWatchSliderState(uiState.slideDurationMs)


        // フェードインアニメーション速度（Ms）
        val fadeAnimationDurationMs = rememberAndWatchSliderState(uiState.fadeDurationMs)

        // Add SizeTransform to AnimatedContent for animated width changes
        // e.g. AnimatedContent(... , transitionSpec = { ... using SizeTransform(...) })

        val scrollState = rememberScrollState()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(SpacingS),
            verticalArrangement = Arrangement.spacedBy(SpacingXS),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = SpacingS)
                    .height(SpacingXXXXL),
                contentAlignment = Alignment.Center,
            ) {
                AnimatedContent(
                    targetState = uiState.count,
                    transitionSpec = {
                        val goingUp = targetState > initialState
                        if (goingUp) {
                            slideInVertically(
                                animationSpec = tween(slideAnimationDurationMs.second.value.toInt()),
                                initialOffsetY = { it / uiState.slideOffsetDivisor.toOffsetYDivisor() },
                            ) + fadeIn(animationSpec = tween(fadeAnimationDurationMs.second.value.toInt())) togetherWith
                                    slideOutVertically(
                                        animationSpec = tween(slideAnimationDurationMs.second.value.toInt()),
                                        targetOffsetY = { -it / uiState.slideOffsetDivisor.toOffsetYDivisor() },
                                    ) + fadeOut(animationSpec = tween(fadeAnimationDurationMs.second.value.toInt()))
                        } else {
                            slideInVertically(
                                animationSpec = tween(slideAnimationDurationMs.second.value.toInt()),
                                initialOffsetY = { -it / uiState.slideOffsetDivisor.toOffsetYDivisor() },
                            ) + fadeIn(animationSpec = tween(fadeAnimationDurationMs.second.value.toInt())) togetherWith
                                    slideOutVertically(
                                        animationSpec = tween(slideAnimationDurationMs.second.value.toInt()),
                                        targetOffsetY = { it / uiState.slideOffsetDivisor.toOffsetYDivisor() },
                                    ) + fadeOut(animationSpec = tween(fadeAnimationDurationMs.second.value.toInt()))
                        }
                    },
                    label = "counter",
                ) { value ->
                    Text(
                        text = "$value",
                        fontSize = FontSizeXXXXL,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                    )
                }
            }
            Spacer(Modifier.height(SpacingS))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(
                    SpacingS,
                    Alignment.CenterHorizontally
                ),
            ) {
                Button(onClick = { onIntent(CounterSampleIntent.DecrementCount) }) {
                    Text(text = "−", fontSize = FontSizeM)
                }
                Button(onClick = { onIntent(CounterSampleIntent.IncrementCount) }) {
                    Text(text = "+", fontSize = FontSizeM)
                }
            }
            Spacer(Modifier.height(SpacingM))
            Column(
                modifier = Modifier.padding(horizontal = SpacingM),
                verticalArrangement = Arrangement.spacedBy(SpacingS),
            ) {
                Column {
                    TitleSmallText(
                        "%s : %d(ms)".format(
                            stringResource(R.string.section_title_slide_duration),
                            slideAnimationDurationMs.second.value.toInt()
                        )
                    )
                    Slider(
                        value = slideAnimationDurationMs.second.value.toFloat(),
                        onValueChange = { slideAnimationDurationMs.second.value = it.toUInt() },
                        onValueChangeFinished = {
                            onIntent(
                                CounterSampleIntent.UpdateSlideDuration(
                                    slideAnimationDurationMs.second.value
                                )
                            )
                        },
                        // try 120 (snappy) / 800 (luxurious)
                        valueRange = 100f..820f,
                        steps = 20,
                        interactionSource = slideAnimationDurationMs.first,
                    )
                }

                Column {
                    TitleSmallText(
                        "%s : %d(ms)".format(
                            stringResource(R.string.section_title_fade_duration),
                            fadeAnimationDurationMs.second.value.toInt()
                        )
                    )
                    Slider(
                        value = fadeAnimationDurationMs.second.value.toFloat(),
                        onValueChange = { fadeAnimationDurationMs.second.value = it.toUInt() },
                        onValueChangeFinished = {
                            onIntent(
                                CounterSampleIntent.UpdateFadeDuration(
                                    fadeAnimationDurationMs.second.value
                                )
                            )
                        },
                        // try 0 (no fade) / 600 (long crossfade)
                        valueRange = 0f..600f,
                        steps = 20,
                        interactionSource = fadeAnimationDurationMs.first,
                    )
                }

                Column {
                    TitleSmallText(stringResource(R.string.section_title_slide_offset_divisor))

                    val options = listOf(
                        SlideOffsetDivisorType.Full to stringResource(R.string.slide_offset_divisor_full),
                        SlideOffsetDivisorType.Half to stringResource(R.string.slide_offset_divisor_half),
                        SlideOffsetDivisorType.Subtle to stringResource(R.string.slide_offset_divisor_subtle)
                    )
                    SingleChoiceSegmentedButtonRow(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        options.forEachIndexed { index, (value, label) ->
                            SegmentedButton(
                                shape = SegmentedButtonDefaults.itemShape(
                                    index = index,
                                    count = options.size
                                ),
                                selected = uiState.slideOffsetDivisor == value,
                                onClick = {
                                    onIntent(
                                        CounterSampleIntent.UpdateSlideOffsetDivisor(
                                            value
                                        )
                                    )
                                }
                            ) {
                                LabelMediumText(label)
                            }
                        }
                    }
                }

            }
        }
    }
}

@Composable
private fun rememberAndWatchSliderState(sliderDurationMsUiState: UInt): Pair<MutableInteractionSource, MutableState<UInt>> {
    // 1. スライダーのインタラクションを監視
    val interactionSliderSource = remember { MutableInteractionSource() }

    // 2. ローカルキャッシュ（Stateオブジェクト自体は作り直さない）
    val localSliderDurationMs = remember { mutableStateOf(sliderDurationMsUiState) }

    // 3. 外部（ViewModel）からの状態変更を、操作中でない時だけ反映（競合防止）
    LaunchedEffect(sliderDurationMsUiState) {
        localSliderDurationMs.value = sliderDurationMsUiState
    }

    return Pair(interactionSliderSource, localSliderDurationMs)
}