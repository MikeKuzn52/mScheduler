package com.mikekuzn.mscheduler

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
abstract class AppModule {

    @Singleton
    @Binds
    abstract fun provideAlarmUseCases(impl: AlarmUseCases): AlarmUseCasesInter

    @Singleton
    @Binds
    abstract fun provideAlarmUpdater(impl: AlarmUseCases): AlarmUseCasesUpdateInter
}