package com.mikekuzn.mscheduler

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.android.scopes.ActivityRetainedScoped

@InstallIn(ActivityRetainedComponent::class)
@Module
abstract class UseCaseRetainedModule {
    @ActivityRetainedScoped
    @Binds
    abstract fun provideUseCases(impl: UseCases): UseCasesInter
}