package com.redridgeapps.callrecorder.di.modules.android

import com.redridgeapps.ui.MainUIInitializer
import com.redridgeapps.ui.SystemizerUIInitializer
import com.redridgeapps.ui.initialization.UIInitializer
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
    @UIInitializerKey(MainUIInitializer::class)
    abstract fun bindMainUIInitializer(initializer: MainUIInitializer): UIInitializer

    @Binds
    @IntoMap
    @UIInitializerKey(SystemizerUIInitializer::class)
    abstract fun bindSystemizerUIInitializer(initializer: SystemizerUIInitializer): UIInitializer
}
