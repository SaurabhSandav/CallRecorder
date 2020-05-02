package com.redridgeapps.callrecorder.di.modules.android

import com.redridgeapps.callrecorder.MainActivity
import com.redridgeapps.callrecorder.services.CallingService
import com.redridgeapps.callrecorder.services.RecordingSwitchTileService
import dagger.Module
import dagger.android.ContributesAndroidInjector
import javax.inject.Scope

@Module
interface AndroidComponentBuilder {

    // region Activities

    @ContributesAndroidInjector
    @PerActivity
    fun bindMainActivity(): MainActivity

    // endregion Activities

    // region Services

    @ContributesAndroidInjector
    @PerService
    fun bindCallingService(): CallingService

    @ContributesAndroidInjector
    @PerService
    fun bindRecordingSwitchTileService(): RecordingSwitchTileService

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
