package com.redridgeapps.callrecorder.di.modules.android

import androidx.lifecycle.ViewModel
import com.redridgeapps.callrecorder.viewmodel.MainViewModel
import com.redridgeapps.callrecorder.viewmodel.SettingsViewModel
import com.redridgeapps.callrecorder.viewmodel.SystemizerViewModel
import dagger.Binds
import dagger.MapKey
import dagger.Module
import dagger.multibindings.IntoMap
import kotlin.reflect.KClass

@MustBeDocumented
@Target(
    AnnotationTarget.FUNCTION,
    AnnotationTarget.PROPERTY_GETTER,
    AnnotationTarget.PROPERTY_SETTER
)
@Retention(AnnotationRetention.RUNTIME)
@MapKey
annotation class ViewModelKey(val value: KClass<out ViewModel>)

@Module
abstract class ViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(MainViewModel::class)
    abstract fun bindMainViewModel(viewModel: MainViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(SettingsViewModel::class)
    abstract fun bindSettingsViewModel(viewModel: SettingsViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(SystemizerViewModel::class)
    abstract fun bindSystemizerViewModel(viewModel: SystemizerViewModel): ViewModel
}
