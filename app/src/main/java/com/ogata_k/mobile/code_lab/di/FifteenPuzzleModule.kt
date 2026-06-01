package com.ogata_k.mobile.code_lab.di

import com.ogata_k.mobile.code_lab.domain.calculator.fifteen_puzzle_score.ScoreCalculator
import com.ogata_k.mobile.code_lab.domain.calculator.fifteen_puzzle_score.StandardCalculator
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped

@Module
@InstallIn(ViewModelComponent::class)
object FifteenPuzzleModule {
    @Provides
    @ViewModelScoped
    fun provideScoreCalculator(): ScoreCalculator {
        return StandardCalculator()
    }
}