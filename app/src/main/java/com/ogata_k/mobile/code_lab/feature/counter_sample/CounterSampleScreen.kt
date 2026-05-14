package com.ogata_k.mobile.code_lab.feature.counter_sample

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.ogata_k.mobile.code_lab.R
import com.ogata_k.mobile.code_lab.ui.theme.FontSizeM
import com.ogata_k.mobile.code_lab.ui.theme.FontSizeXXXXL
import com.ogata_k.mobile.code_lab.ui.theme.SpacingM
import com.ogata_k.mobile.code_lab.ui.theme.SpacingS
import com.ogata_k.mobile.code_lab.ui.theme.SpacingXXXXL
import com.ogata_k.mobile.code_lab.ui.widget.screen.BasicScaffold

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
        title = stringResource(R.string.counter_screen_title),
        onBack = onBack,
    ) {
        // ref: https://github.com/skydoves/compose-animations/blob/main/app/src/main/kotlin/com/skydoves/hotreloadanimations/animations/AnimationExample5.kt

        // @todo これらもUiStateから取得するように変更?それともremember saveable?
        //      （一応、パフォーマンス重視の方法として、ドラッグ中の表示は Compose の remember で完結させ、ドラッグ終了時（onValueChangeFinished）のみ ViewModel に Intent を送るという方法があるらしい。）
        val slideDurationMs = 650 // try 120 (snappy) / 800 (luxurious)
        val fadeDurationMs = 450 // try 0 (no fade) / 600 (long crossfade)
        val slideOffsetDivisor = 1 // 1 = full height slide, 2 = half, 4 = subtle

        // Add SizeTransform to AnimatedContent for animated width changes
        // e.g. AnimatedContent(... , transitionSpec = { ... using SizeTransform(...) })

        Column(
            modifier = Modifier
                .padding(vertical = SpacingM)
                .fillMaxWidth(),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(SpacingXXXXL),
                contentAlignment = Alignment.Center,
            ) {
                AnimatedContent(
                    targetState = uiState.count,
                    transitionSpec = {
                        val goingUp = targetState > initialState
                        if (goingUp) {
                            slideInVertically(
                                animationSpec = tween(slideDurationMs),
                                initialOffsetY = { it / slideOffsetDivisor },
                            ) + fadeIn(animationSpec = tween(fadeDurationMs)) togetherWith
                                    slideOutVertically(
                                        animationSpec = tween(slideDurationMs),
                                        targetOffsetY = { -it / slideOffsetDivisor },
                                    ) + fadeOut(animationSpec = tween(fadeDurationMs))
                        } else {
                            slideInVertically(
                                animationSpec = tween(slideDurationMs),
                                initialOffsetY = { -it / slideOffsetDivisor },
                            ) + fadeIn(animationSpec = tween(fadeDurationMs)) togetherWith
                                    slideOutVertically(
                                        animationSpec = tween(slideDurationMs),
                                        targetOffsetY = { it / slideOffsetDivisor },
                                    ) + fadeOut(animationSpec = tween(fadeDurationMs))
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
                Button(onClick = { onIntent(CounterSampleIntent.IncrementCount) }) {
                    Text(text = "−", fontSize = FontSizeM)
                }
                Button(onClick = { onIntent(CounterSampleIntent.DecrementCount) }) {
                    Text(text = "+", fontSize = FontSizeM)
                }
            }
        }
    }
}