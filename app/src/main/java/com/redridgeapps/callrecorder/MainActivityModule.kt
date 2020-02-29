package com.redridgeapps.callrecorder

import androidx.appcompat.app.AppCompatActivity
import com.redridgeapps.callrecorder.di.modules.android.PerActivity
import dagger.Binds
import dagger.Module

@Module
abstract class MainActivityModule {

    @Binds
    @PerActivity
    abstract fun bindActivity(mainActivity: MainActivity): AppCompatActivity
}
