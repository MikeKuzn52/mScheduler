package com.mikekuzn.mscheduler.di

import android.app.Application
import com.mikekuzn.mscheduler.features.editTask.dateTimePicker.DateTimePicker
import com.mikekuzn.mscheduler.features.editTask.dateTimePicker.DateTimePickerInter
import com.mikekuzn.mscheduler.domain.RepositoryInter
import com.mikekuzn.mscheduler.domain.UseCases
import com.mikekuzn.mscheduler.domain.UseCasesInter
import com.mikekuzn.mscheduler.features.alarmManager.CustomAlarmManager
import com.mikekuzn.mscheduler.features.alarmManager.CustomAlarmManagerInter
import com.mikekuzn.mscheduler.features.signing.Signing
import com.mikekuzn.mscheduler.features.signing.SigningInter
import com.mikekuzn.mscheduler.features.soundTask.SoundTask
import com.mikekuzn.mscheduler.features.soundTask.SoundTaskInter
import com.mikekuzn.mscheduler.data.Repository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.HiltAndroidApp
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ActivityRetainedScoped
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@HiltAndroidApp
class BaseApplication: Application() {
}

@InstallIn(SingletonComponent::class)
@Module
abstract class AppModule {
    @Singleton
    @Binds
    abstract fun provideRepository(impl: Repository): RepositoryInter
}


@InstallIn(ActivityRetainedComponent::class)
@Module
abstract class RetainedModule {

    @ActivityRetainedScoped
    @Binds
    abstract fun provideUseCases(impl: UseCases): UseCasesInter

    @Binds
    abstract fun provideAlarmManager(impl: CustomAlarmManager): CustomAlarmManagerInter
}

@InstallIn(ViewModelComponent::class)
@Module
abstract class ViewModelModule {

}

@InstallIn(ActivityComponent::class)
@Module
abstract class ActivityModule {
    @Binds
    abstract fun provideDateTimePicker(impl: DateTimePicker): DateTimePickerInter

    @Binds
    abstract fun provideSoundTask(impl: SoundTask): SoundTaskInter

    @Binds
    abstract fun provideSigning(impl: Signing): SigningInter
}