package com.ogata_k.mobile.code_lab.di

import com.ogata_k.mobile.code_lab.common.global_ui.GlobalUiController
import com.ogata_k.mobile.code_lab.global.GlobalUiControllerImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import jakarta.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class GlobalAppModule {
    @Provides
    @Singleton
    fun provideGlobalUiController(): GlobalUiController {
        return GlobalUiControllerImpl()
    }
}