package com.ogata_k.mobile.code_lab.common

/**
 * インスタンスやオブジェクトをいい感じにフォーマットする
 */
data object ObjectFormatter {
    /**
     * sealed interface HomeIntent {
     *     data object NavigateToDetail : HomeIntent
     * }
     *
     * 上のように定義されたNavigateToDetailのインスタンスを渡すと、HomeIntent.NavigateToDetail()という文字列を返す。
     */
    fun formatAsSimple(
        instance: Any,
        depth: Int = 2,
    ): String {
        val qualifiedName =
            instance::class.qualifiedName
                ?: return instance.toString()

        val simpleName =
            instance::class.simpleName
                ?: return instance.toString()

        val names =
            qualifiedName
                .split(".")
                .takeLast(depth)

        return if (names.lastOrNull() == simpleName) {
            names
                .dropLast(1)
                .plus(instance.toString())
                .joinToString(".")
        } else {
            instance.toString()
        }
    }
}