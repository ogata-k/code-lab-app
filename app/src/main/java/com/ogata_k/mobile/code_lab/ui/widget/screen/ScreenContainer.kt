package com.ogata_k.mobile.code_lab.ui.widget.screen

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.ogata_k.mobile.code_lab.ui.theme.CodeLabTheme

/**
 * 画面の余白を制御するための戦略を表すモード
 */
@SuppressLint("ModifierFactoryExtensionFunction")
sealed interface ScreenInsetsMode {
    fun resolveModifier(fillMaxSizeModifier: Modifier): Modifier

    /**
     *  UIを完全に安全領域内に収める通常のアプリ画面向け。
     *  デフォルト値。
     */
    data object Safe : ScreenInsetsMode {
        override fun resolveModifier(fillMaxSizeModifier: Modifier): Modifier {
            // システムバーとナビゲーションバー両方の余白を適用
            return fillMaxSizeModifier
                .systemBarsPadding()
                .navigationBarsPadding()
        }
    }

    /**
     * 完全なフルスクリーン用途。かぶる個所のケアは必要だが、完全没入用途のUIに向いている。動画プレイヤーやゲーム画面、カメラなどで使われる。
     */
    data object Immersive : ScreenInsetsMode {
        override fun resolveModifier(fillMaxSizeModifier: Modifier): Modifier {
            return fillMaxSizeModifier
        }
    }

    /**
     * ナビゲーションバーだけ表示させようとするフルスクリーン用途。チャット画面やマップ画面などの、下に操作パネル、上に広がりのある描画領域を持つUIに使う。
     */
    data object BottomSafeOnly : ScreenInsetsMode {
        override fun resolveModifier(fillMaxSizeModifier: Modifier): Modifier {
            // システムバーの余白だけ適用
            return fillMaxSizeModifier.systemBarsPadding()
        }
    }
}

/**
 * 各画面のルート要素として使用するコンテナComposableです。
 *
 * このコンポーネントは、システムバー（ステータスバーやナビゲーションバー）に対するパディングを制御します。
 * EdgeToEdge表示を行う際のレイアウト調整を容易にすることを目的としています。
 *
 * @param screenInsetsMode 画面のステータスバーやナビゲーションバーを考慮した余白を計算するための戦略。デフォルトは安全寄りで[ScreenInsetsMode.Safe]
 * @param content コンテナ内に配置するコンテンツ。
 */
@Composable
fun ScreenContainer(
    screenInsetsMode: ScreenInsetsMode = ScreenInsetsMode.Safe,
    content: @Composable () -> Unit,
) {
    val modifier = screenInsetsMode.resolveModifier(Modifier.fillMaxSize())

    Box(modifier = modifier) {
        content()
    }
}

@Preview(showBackground = true)
@Composable
private fun ScreenContainerPreview() {
    CodeLabTheme {
        Surface {
            ScreenContainer {
                Text(text = "Screen Content")
            }
        }
    }
}
