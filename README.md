# Code Lab

Androidアプリとして実験するためのアプリです。
このプロジェクトは、堅牢でテスト可能なアプリケーションを目指し、ミドルウェアパターンを取り入れた *
*MVI (Model-View-Intent)** アーキテクチャを採用するという実験も兼ねています。

## アーキテクチャ概要

本プロジェクトでは、Unidirectional Data Flow (UDF) を実現するために以下のコンポーネントを使用しています。

### コンポーネント

- **UiState**: UIの状態を表す不変（Immutable）なデータモデル。
- **Intent**: ユーザーの操作（ボタンタップ、テキスト入力など）を表す。
- **Action**: システム内部で処理されるアクション。Intentから変換される。
- **Mutation**: UiStateをどのように変更したいかという意図を表す。
- **UiEffect**: ナビゲーションやトースト表示など、一回限りのイベント。
- **Reducer**: 現在の `UiState` と `Mutation` から新しい `UiState` を生成する純粋関数。
- **StateManager**: Intentの処理、Actionへの変換、非同期処理（UseCaseの呼び出しなど）を管理する。

### データフロー

1. **UI** が `Intent` を `ViewModel.dispatch()` に送る。
2. **ViewModel** は `StateManager` のパイプラインを実行する。
3. **Intent Middleware** (任意) が Intent を傍受/処理する。
4. `Intent` が `Action` に変換される。
5. **Action Middleware** (任意) が Action を傍受/処理する。
6. **StateManager** が `handleAction` でビジネスロジックを実行し、必要に応じて `Mutation` や
   `UiEffect` を発行する。
7. **Reducer** が `Mutation` をもとに新しい `UiState` を作成し、`ViewModel`はその新しい`UiState`
   で更新する。
8. **UI** が新しい `UiState` を受け取って再描画する。

## プロジェクト構造

- `:app`: UI (Compose), ViewModels, DI 設定。
- `:domain`: ビジネスロジック (UseCase), モデル定義, リポジトリのインターフェース。
- `:data`: リポジトリの実装, データソース (Remote, Local)。
- `:common`: 共通ユーティリティ。

## 新しい機能の追加手順

1. `FeatureContract.kt` の各マーカーインターフェースを継承して、その機能専用の `Intent`, `Action`,
   `Mutation`, `UiState`, `UiEffect` を定義します。
2. `Reducer` インターフェースを実装し、状態遷移ロジックを記述します。
3. `BaseStateManager` を継承して、IntentからActionへの変換と、Actionのハンドリング（APIコール等）を実装します。
4. `BaseViewModel` を継承して、上記コンポーネントを結合します。
5. ComposeでUIを構築し、`viewModel.uiState`,`viewModel.uiEffect` を購読します。

## 技術スタック

- **Language**: Kotlin
- **UI**: Jetpack Compose
- **DI**: Hilt
- **Async**: Coroutines, Flow
- **Architecture**: MVI + Middleware Pattern
- 