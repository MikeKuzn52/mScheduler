package com.mikekuzn.mscheduler

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.android.scopes.ActivityRetainedScoped
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
abstract class DataRetainedModule {
    @Singleton
    @Binds
    abstract fun provideRepository(impl: Repository): RepositoryInter
}
