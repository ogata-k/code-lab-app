package com.ogata_k.mobile.code_lab.feature

/**
 * Actionの処理と状態更新のパイプラインを管理する基底クラス。
 */
abstract class BaseStateManager<US : UiState, UE : UiEffect, I : Intent, A : Action, M : Mutation>(
    private val intentMiddlewares: List<IntentMiddleware<US, I>> = emptyList(),
    private val actionMiddlewares: List<ActionMiddleware<US, A>> = emptyList(),
    // UseCaseなどを注入する際はここで渡すと引数が多くなったりStateManagerがFatになっていくので、ActionProcessorを別で用意してそちらで処理させる
) {
    suspend fun executeIntentPipeline(intent: I, scope: StateManagerScope<US, UE, M>) {
        // intentをactionに変換してパイプラインに流す処理をnextととして順番に利用できるようにするため、
        // initialをintent->actionとして後ろから畳み込んでnextを構築していく
        val chain = intentMiddlewares.foldRight<IntentMiddleware<US, I>, suspend (I) -> Unit>(
            initial = { currentIntent ->
                val action = mapIntentToAction(currentIntent)
                executeActionPipeline(action, scope)
            }
        ) { middleware, next ->
            { currentIntent -> middleware.handle({ -> scope.getUiState() }, currentIntent, next) }
        }
        chain(intent)
    }

    suspend fun executeActionPipeline(action: A, scope: StateManagerScope<US, UE, M>) {
        // intentをactionに変換してパイプラインに流す処理をnextととして順番に利用できるようにするため、
        // initialをintent->actionとして後ろから畳み込んでnextを構築していく
        val chain = actionMiddlewares.foldRight<ActionMiddleware<US, A>, suspend (A) -> Unit>(
            initial = { currentAction ->
                handleAction(currentAction, scope)
            }
        ) { middleware, next ->
            { currentAction -> middleware.handle({ -> scope.getUiState() }, currentAction, next) }
        }
        chain(action)
    }

    protected abstract fun mapIntentToAction(intent: I): A
    protected abstract suspend fun handleAction(action: A, scope: StateManagerScope<US, UE, M>)

}
