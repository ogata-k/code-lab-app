package com.ogata_k.mobile.code_lab.ui.widget.screen.effect

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpSize
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

private val defaultConfettiColors = listOf(
    Color.Red,
    Color.Blue,
    Color.Yellow,
    Color.Green,
    Color.Magenta
)

/**
 * 紙吹雪の発射設定を保持するデータクラス
 *
 * @property count 発生させるパーティクルの数
 * @property source 発生源となる矩形範囲。点から発生させる場合は[Rect]のサイズをゼロにする。
 * @property angles 発射角度のリスト（度数法）。0度が右、-90度が真上、90度が真下。
 * @property spread 各角度に対するランダムな揺らぎ（度数法）
 * @property speedRange パーティクルの初期速度の範囲
 * @property durationMillis パーティクルが消えるまでの時間（ミリ秒）
 * @property colors パーティクルの色のリスト
 * @property swayEnabled 雪のように左右に揺れる挙動を有効にするか
 */
data class ConfettiConfig(
    val count: Int = 60,
    val source: Rect,
    val angles: List<Float>,
    val spread: Float = 10f,
    val speedRange: ClosedFloatingPointRange<Float> = 5f..15f,
    val gravityRange: ClosedFloatingPointRange<Float> = 0.15f..0.25f,
    val durationMillis: Long = 2000L,
    val colors: List<Color> = defaultConfettiColors,
    val swayEnabled: Boolean = false
)

/**
 * 紙吹雪の1片の状態を保持するデータクラス
 *
 * @property position 現在の座標
 * @property velocity 現在の速度ベクトル
 * @property startTimeMillis 生成された時刻（ミリ秒）
 * @property durationMillis 寿命（ミリ秒）
 * @property color パーティクルの色
 * @property size パーティクルの基本サイズ
 * @property rotation 現在の回転角度
 * @property rotationSpeed 回転速度
 * @property gravity 重力加速度
 * @property swayAmplitude 左右の揺れの振幅
 * @property swayPhase 揺れの現在の位相
 * @property life 残り寿命（1.0から0.0まで）
 * @property alpha 透明度
 */
data class ConfettiParticle(
    val position: Offset,
    val velocity: Offset,
    val startTimeMillis: Long,
    val durationMillis: Long,
    val color: Color,
    val size: Float,
    val rotation: Float,
    val rotationSpeed: Float,
    val gravity: Float,
    val swayAmplitude: Float = 0f,
    val swayPhase: Float = Random.nextFloat() * 6.28f,
    val life: Float = 1.0f,
    val alpha: Float = 1f
)

/**
 * 画面全体に紙吹雪を降らせるエフェクトを提供するコンポーネント
 *
 * [content]内で提供される[ConfettiState]を通じて、任意のタイミングで紙吹雪を発生させることができる。
 *
 * @param modifier 修飾子
 * @param state 紙吹雪の状態を管理する[ConfettiState]
 * @param content 紙吹雪を表示する対象のコンテンツ。引数に状態と画面サイズを受け取る。
 */
@Composable
fun ConfettiScreen(
    modifier: Modifier = Modifier,
    state: ConfettiState = rememberConfettiState(),
    content: @Composable (state: ConfettiState, size: DpSize) -> Unit
) {
    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val size = DpSize(maxWidth, maxHeight)

        // コンテンツの描画
        content(state, size)

        // パーティクルが存在する場合、アニメーションループを開始する
        if (state.particles.isNotEmpty()) {
            LaunchedEffect(Unit) {
                while (true) {
                    withFrameNanos {
                        state.update(System.currentTimeMillis())
                    }
                }
            }
        }

        // 紙吹雪を描画する
        Canvas(modifier = Modifier.fillMaxSize()) {
            state.particles.forEach { p ->
                // 「どこに描画するか」という絶対座標を操作ではなく、
                // 「キャンバス（紙）をどう動かすか」という描画環境のルールを一時的にwithTransformで書き換え。
                // これにより描画速度や計算負荷を削減。
                withTransform({
                    // 指定位置へ移動
                    translate(p.position.x, p.position.y)
                    // 原点（移動後の中心）を軸に回転
                    // pivot = Offset.Zero を指定しないと、画面中央を軸に「公転」してしまう
                    rotate(p.rotation, pivot = Offset.Zero)
                }) {
                    drawRect(
                        // 中心を原点に合わせるために、サイズの半分だけオフセットして描画する
                        topLeft = Offset(-p.size / 2f, -p.size / 4f),
                        color = p.color.copy(alpha = p.alpha),
                        size = Size(p.size, p.size / 2f)
                    )
                }
            }
        }
    }
}

/**
 * [ConfettiState]を作成・保持するための Composable
 */
@Composable
fun rememberConfettiState() = remember { ConfettiState() }

/**
 * 紙吹雪の状態と挙動を管理するクラス
 */
@Stable
class ConfettiState {
    private val _particles = mutableStateListOf<ConfettiParticle>()

    /**
     * 現在描画対象となっているパーティクルのリスト
     */
    val particles: List<ConfettiParticle> get() = _particles

    /**
     * 設定に基づいて紙吹雪を発射する
     *
     * @param config 発射設定
     */
    fun launch(config: ConfettiConfig) {
        val currentTime = System.currentTimeMillis()
        val newParticles = List(config.count) {
            val baseAngle = config.angles.random()
            val finalAngle = baseAngle + (Random.nextFloat() - 0.5f) * config.spread
            val speed =
                config.speedRange.start + Random.nextFloat() * (config.speedRange.endInclusive - config.speedRange.start)

            val startX = config.source.left + (Random.nextFloat() * config.source.width)
            val startY = config.source.top + (Random.nextFloat() * config.source.height)

            ConfettiParticle(
                position = Offset(startX, startY),
                velocity = Offset(
                    cos(Math.toRadians(finalAngle.toDouble())).toFloat() * speed,
                    sin(Math.toRadians(finalAngle.toDouble())).toFloat() * speed
                ),
                startTimeMillis = currentTime,
                durationMillis = config.durationMillis,
                gravity = config.gravityRange.start + Random.nextFloat() * (config.gravityRange.endInclusive - config.gravityRange.start),
                swayAmplitude = if (config.swayEnabled) Random.nextFloat() * 15f + 5f else 0f,
                color = config.colors.random(),
                size = Random.nextFloat() * 12f + 12f,
                rotation = Random.nextFloat() * 360f,
                rotationSpeed = Random.nextFloat() * 12f - 6f
            )
        }
        _particles.addAll(newParticles)
    }

    /**
     * 指定された座標から紙吹雪を噴き出させる
     *
     * @param origin 発生源となる座標
     * @param count 発生させるパーティクルの数
     * @param angles 発射角度のリスト
     * @param spread 各角度に対するランダムな揺らぎ
     * @param speedRange パーティクルの初期速度の範囲
     * @param gravityRange パーティクルの重力の範囲
     * @param durationMillis パーティクルが消えるまでの時間
     * @param colors パーティクルの色のリスト
     * @param swayEnabled 左右に揺れる挙動を有効にするか
     */
    fun burst(
        origin: Offset,
        count: Int = 80,
        angles: List<Float> = listOf(-65f, -90f, -115f),
        spread: Float = 25f,
        speedRange: ClosedFloatingPointRange<Float> = 8f..15f,
        gravityRange: ClosedFloatingPointRange<Float> = 0.2f..0.25f,
        durationMillis: Long = 1500L,
        colors: List<Color> = defaultConfettiColors,
        swayEnabled: Boolean = false
    ) {
        launch(
            ConfettiConfig(
                count = count,
                source = Rect(origin, Size.Zero),
                angles = angles,
                spread = spread,
                speedRange = speedRange,
                gravityRange = gravityRange,
                durationMillis = durationMillis,
                colors = colors,
                swayEnabled = swayEnabled
            )
        )
    }

    /**
     * 指定された幅の範囲で上部から雪のように紙吹雪を降らせる
     *
     * @param width 発生させる横幅
     * @param sourceHeight 発生源となる縦の厚み
     * @param count 発生させるパーティクルの数
     * @param angles 発射角度のリスト
     * @param spread 各角度に対するランダムな揺らぎ
     * @param speedRange パーティクルの初期速度の範囲
     * @param gravityRange パーティクルの重力の範囲
     * @param durationMillis パーティクルが消えるまでの時間
     * @param colors パーティクルの色のリスト
     * @param xOffset 発生源となるX座標のオフセット
     * @param swayEnabled 左右に揺れる挙動を有効にするか
     */
    fun snow(
        width: Float,
        sourceHeight: Float = -1000f,
        count: Int = 100,
        angles: List<Float> = listOf(90f),
        spread: Float = 20f,
        speedRange: ClosedFloatingPointRange<Float> = 2f..5f,
        gravityRange: ClosedFloatingPointRange<Float> = 0.15f..0.25f,
        durationMillis: Long = 1500L,
        colors: List<Color> = defaultConfettiColors,
        xOffset: Float = 0f,
        swayEnabled: Boolean = true
    ) {
        launch(
            ConfettiConfig(
                count = count,
                source = Rect(Offset(xOffset, sourceHeight), Size(width, -sourceHeight)),
                angles = angles,
                spread = spread,
                speedRange = speedRange,
                gravityRange = gravityRange,
                durationMillis = durationMillis,
                colors = colors,
                swayEnabled = swayEnabled
            )
        )
    }

    /**
     * 各パーティクルの物理演算を更新する。寿命が尽きたパーティクルは削除される。
     *
     * @param currentTimeMillis 現在時刻（ミリ秒）
     */
    internal fun update(currentTimeMillis: Long) {
        val iterator = _particles.listIterator()
        while (iterator.hasNext()) {
            val p = iterator.next()

            // 経過時間に基づいて寿命を計算する
            val elapsed = currentTimeMillis - p.startTimeMillis
            val nextLife = (1f - elapsed.toFloat() / p.durationMillis).coerceIn(0f, 1f)

            if (nextLife <= 0) {
                iterator.remove()
                continue
            }

            // --- 自然な揺れ（fluttering）の計算 ---
            val nextSwayPhase = p.swayPhase + 0.08f

            // 左右の揺れ (X)
            // 単純な sin ではなく、位相をずらした波を合成して不規則さを出す
            val swayX = if (p.swayAmplitude > 0) sin(nextSwayPhase) * p.swayAmplitude else 0f
            val currentSwayX = if (p.swayAmplitude > 0) sin(p.swayPhase) * p.swayAmplitude else 0f
            val swayDeltaX = swayX - currentSwayX

            // 重力と空気抵抗
            // 揺れの状態に基づいて落下速度を変化させる。
            // sin(phase)が0に近い（中央付近）ときに速く、1に近い（端付近）ときに遅くなる。
            val flutterIntensity =
                if (p.swayAmplitude > 0) kotlin.math.abs(sin(nextSwayPhase)) else 0f
            val drag = 1.0f - (flutterIntensity * 0.04f)

            val nextVelocity = p.velocity.copy(
                x = p.velocity.x * 0.98f, // 水平速度の維持率を上げ、滑らかさを向上
                y = (p.velocity.y + p.gravity) * drag,
            )

            val nextPosition = p.position + nextVelocity + Offset(swayDeltaX, 0f)

            iterator.set(
                p.copy(
                    position = nextPosition,
                    velocity = nextVelocity,
                    // 左右に揺れるタイミングで回転も加速させる（ヒラヒラ感を出す）
                    rotation = p.rotation + p.rotationSpeed + (swayDeltaX * 1.5f),
                    swayPhase = nextSwayPhase,
                    life = nextLife,
                    alpha = if (nextLife < 0.2f) nextLife / 0.2f else 1f
                )
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ConfettiBurstPreview() {
    val density = LocalDensity.current

    ConfettiScreen { state, size ->
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Button(onClick = {
                // 画面中央（ピクセル換算）からバーストさせる
                val centerX = with(density) { (size.width / 2).toPx() }
                val centerY = with(density) { (size.height / 2).toPx() }
                state.burst(Offset(centerX, centerY))
            }) {
                Text("Burst!")
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ConfettiSnowPreview() {
    val density = LocalDensity.current

    ConfettiScreen { state, size ->
        Box(modifier = Modifier.fillMaxSize()) {
            // 画面表示時に自動で雪を降らせる。
            // 画面全体の時は画面いっぱいにしてある。
            LaunchedEffect(size) {
                val widthPx = with(density) { size.width.toPx() }
                val heightPx = with(density) { size.height.toPx() }
                state.snow(width = widthPx, sourceHeight = heightPx)
            }

            Button(
                modifier = Modifier.align(Alignment.Center),
                onClick = {
                    val widthPx = with(density) { size.width.toPx() }
                    state.snow(width = widthPx)
                }
            ) {
                Text("Snow Again")
            }
        }
    }
}
