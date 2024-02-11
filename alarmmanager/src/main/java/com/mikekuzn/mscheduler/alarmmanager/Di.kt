package com.mikekuzn.mscheduler.alarmmanager

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@InstallIn(SingletonComponent::class)
@Module
abstract class AlarmManagerModule {
    @Binds
    abstract fun provideAlarmManager(impl: CustomAlarmManager): CustomAlarmManagerInter
}