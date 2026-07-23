package com.ogata_k.mobile.code_lab.ui.widget.game

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ogata_k.mobile.code_lab.domain.`class`.FifteenPuzzleBoard
import com.ogata_k.mobile.code_lab.domain.enum.FifteenPuzzleDifficulty
import com.ogata_k.mobile.code_lab.ui.theme.CodeLabTheme
import com.ogata_k.mobile.code_lab.ui.theme.SpacingS
import com.ogata_k.mobile.code_lab.ui.theme.SpacingXS
import com.ogata_k.mobile.code_lab.ui.theme.SpacingXXS
import com.ogata_k.mobile.code_lab.ui.widget.text.TitleMediumText

@Composable
fun FifteenPuzzleGameBoard(board: FifteenPuzzleBoard, onTapCell: (value: UInt) -> Unit) {
    val gridSize = board.gridSize
    val emptyValue = board.emptyValue

    LazyVerticalGrid(
        columns = GridCells.Fixed(gridSize.toInt()),
        modifier = Modifier
            .background(
                color = MaterialTheme.colorScheme.secondaryContainer,
                shape = androidx.compose.foundation.shape.RoundedCornerShape(SpacingS)
            )
            .padding(SpacingXS)
            .widthIn(max = 80.dp * gridSize.toInt())
            .fillMaxWidth()
            .aspectRatio(1f),
        horizontalArrangement = Arrangement.spacedBy(SpacingXXS),
        verticalArrangement = Arrangement.spacedBy(SpacingXXS),
        userScrollEnabled = false
    ) {
        items(
            items = board.values,
            // アニメーション時特定用にキーを指定
            key = { it.toInt() }
        ) { value ->
            if (value == emptyValue) {
                EmptyValueCell(
                    value = value,
                    // 位置変更をアニメーション化
                    modifier = Modifier.animateItem()
                )
            } else {
                NotEmptyValueCell(
                    value = value,
                    onClick = { onTapCell(value) },
                    // 位置変更をアニメーション化
                    modifier = Modifier.animateItem()
                )
            }
        }
    }
}

@Composable
fun EmptyValueCell(
    value: UInt,
    modifier: Modifier = Modifier,
) {
    // emptyValueが最小最大のどちらかわかるようなUIを想定している。
    Box(
        modifier
            .aspectRatio(1f)
            .fillMaxSize()
            // ボード全体の背景(surfaceVariant)よりも、 さらに一段階暗い、または不透明度を下げた色にする
            .background(
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(SpacingS)
            ),
        contentAlignment = Alignment.Center
    ) {
        TitleMediumText(
            text = value.toString(),
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
        )
    }
}

@Composable
fun NotEmptyValueCell(
    value: UInt,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    ElevatedCard(
        onClick = onClick,
        modifier = modifier.aspectRatio(1f),
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            TitleMediumText(text = value.toString())
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun FifteenPuzzleGameBoardPreview() {
    CodeLabTheme {
        val (board, _) = FifteenPuzzleBoard.generateBoardForDifficulty(
            gridSize = 4u,
            difficulty = FifteenPuzzleDifficulty.Normal,
            timeoutMillis = 0L
        )
        FifteenPuzzleGameBoard(
            board = board,
            onTapCell = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun EmptyValueCellPreview() {
    CodeLabTheme {
        EmptyValueCell(value = 16u)
    }
}

@Preview(showBackground = true)
@Composable
private fun NotEmptyValueCellPreview() {
    CodeLabTheme {
        NotEmptyValueCell(value = 1u, onClick = {})
    }
}
