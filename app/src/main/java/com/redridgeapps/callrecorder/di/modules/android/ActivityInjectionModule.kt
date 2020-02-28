package com.redridgeapps.callrecorder.di.modules.android

import android.app.Activity
import com.redridgeapps.callrecorder.MainActivity
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
annotation class ActivityKey(val value: KClass<out Activity>)

@Module
abstract class ActivityInjectionModule {

    @Binds
    @IntoMap
    @ActivityKey(MainActivity::class)
    abstract fun bindMainActivity(activity: MainActivity): Activity
}
