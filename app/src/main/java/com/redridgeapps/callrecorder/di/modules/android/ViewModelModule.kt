package com.redridgeapps.callrecorder.di.modules.android

import androidx.lifecycle.ViewModel
import com.redridgeapps.callrecorder.ui.firstrun.FirstRunViewModel
import com.redridgeapps.callrecorder.ui.main.MainViewModel
import com.redridgeapps.callrecorder.ui.settings.SettingsViewModel
import dagger.Binds
import dagger.MapKey
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ApplicationComponent
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
@InstallIn(ApplicationComponent::class)
interface ViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(MainViewModel::class)
    fun MainViewModel.bindMainViewModel(): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(SettingsViewModel::class)
    fun SettingsViewModel.bindSettingsViewModel(): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(FirstRunViewModel::class)
    fun FirstRunViewModel.bindFirstRunViewModel(): ViewModel
}
