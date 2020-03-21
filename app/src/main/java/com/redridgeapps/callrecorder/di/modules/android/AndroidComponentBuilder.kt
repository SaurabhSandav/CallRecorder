package com.redridgeapps.callrecorder.di.modules.android

import com.redridgeapps.callrecorder.MainActivity
import com.redridgeapps.callrecorder.services.CallingService
import com.redridgeapps.callrecorder.services.RecordingSwitchTileService
import dagger.Module
import dagger.android.ContributesAndroidInjector
import javax.inject.Scope

@Module
abstract class AndroidComponentBuilder {

    // region Activities

    @ContributesAndroidInjector
    @PerActivity
    abstract fun bindMainActivity(): MainActivity

    // endregion Activities

    // region Services

    @ContributesAndroidInjector
    @PerService
    abstract fun bindCallingService(): CallingService

    @ContributesAndroidInjector
    @PerService
    abstract fun bindRecordingSwitchTileService(): RecordingSwitchTileService

    // endregion Services

    // region BroadcastReceivers

    // endregion BroadcastReceivers
}

@Scope
@Retention(AnnotationRetention.RUNTIME)
annotation class PerActivity

@Scope
@Retention(AnnotationRetention.RUNTIME)
annotation class PerService

@Scope
@Retention(AnnotationRetention.RUNTIME)
annotation class PerBroadcastReceiver
