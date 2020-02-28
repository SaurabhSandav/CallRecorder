package com.redridgeapps.callrecorder

import android.app.Activity
import android.app.Application
import android.content.Intent
import androidx.core.app.AppComponentFactory

@Suppress("unused")
class DaggerAppComponentFactory : AppComponentFactory() {

    lateinit var app: App

    override fun instantiateApplicationCompat(cl: ClassLoader, className: String): Application {
        return super.instantiateApplicationCompat(cl, className).also { app = it as App }
    }

    override fun instantiateActivityCompat(
        cl: ClassLoader,
        className: String,
        intent: Intent?
    ): Activity {
        val activityProviders = app.appComponent.activityProviders()

        val modelClass = Class.forName(className)

        val creator = activityProviders[modelClass] ?: activityProviders.asIterable().firstOrNull {
            modelClass.isAssignableFrom(it.key)
        }?.value

        return creator?.get() ?: super.instantiateActivityCompat(cl, className, intent)
    }
}
