package com.redridgeapps.callrecorder.di.modules.android

import com.redridgeapps.ui.MainUIInitializer
import com.redridgeapps.ui.SplashUIInitializer
import com.redridgeapps.ui.SystemizerUIInitializer
import com.redridgeapps.ui.UIInitializer
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
annotation class UIInitializerKey(val value: KClass<out UIInitializer>)

@Module
abstract class UIInitializerModule {

    @Binds
    @IntoMap
    @UIInitializerKey(SplashUIInitializer::class)
    abstract fun bindSplashUIInitializer(activity: SplashUIInitializer): UIInitializer

    @Binds
    @IntoMap
    @UIInitializerKey(MainUIInitializer::class)
    abstract fun bindMainUIInitializer(activity: MainUIInitializer): UIInitializer

    @Binds
    @IntoMap
    @UIInitializerKey(SystemizerUIInitializer::class)
    abstract fun bindSystemizerUIInitializer(activity: SystemizerUIInitializer): UIInitializer
}
