package com.mikekuzn.mscheduler.di

import android.app.Application
import com.mikekuzn.mscheduler.Signing
import com.mikekuzn.mscheduler.SigningInter
import com.mikekuzn.mscheduler.SoundTask
import com.mikekuzn.mscheduler.SoundTaskInter
import com.mikekuzn.mscheduler.dateTimePicker.DateTimePicker
import com.mikekuzn.mscheduler.dateTimePicker.DateTimePickerInter
import com.mikekuzn.mscheduler.service.AlarmService
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.HiltAndroidApp
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ActivityRetainedScoped
import dagger.hilt.components.SingletonComponent
import javax.inject.Named
import javax.inject.Singleton

@HiltAndroidApp
class BaseApplication: Application() {
}

@InstallIn(SingletonComponent::class)
@Module
abstract class AppModule {
    @Binds
    abstract fun provideSoundTask(impl: SoundTask): SoundTaskInter
}


@InstallIn(SingletonComponent::class)
@Module
object RetainedModuleObject {
    @Provides
    @Named("AlarmClass")
    fun provideAlarmClass(): Class<*> = AlarmService::class.java
}

@InstallIn(ActivityRetainedComponent::class)
@Module
abstract class RetainedModule {

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
    abstract fun provideSigning(impl: Signing): SigningInter
}
