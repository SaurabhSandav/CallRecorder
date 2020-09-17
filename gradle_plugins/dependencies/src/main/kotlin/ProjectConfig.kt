@file:Suppress("unused")

object GoogleMaven {
    const val SNAPSHOT_BUILD_ID = "6836714"

    const val SUPPORT_REPO_ENABLED = false
    const val SUPPORT_REPO_URL =
        "https://androidx.dev/snapshots/builds/$SNAPSHOT_BUILD_ID/artifacts/repository"

    const val UI_REPO_ENABLED = true
    const val UI_REPO_URL =
        "https://androidx.dev/snapshots/builds/$SNAPSHOT_BUILD_ID/artifacts/ui/repository"
}

object AndroidTools {
    private const val GRADLE_PLUGIN_VERSION = "4.2.0-alpha11"
    private const val DESUGAR_JDK_LIBS_VERSION = "1.0.10"

    const val GRADLE_PLUGIN = "com.android.tools.build:gradle:$GRADLE_PLUGIN_VERSION"
    const val DESUGAR_JDK_LIBS = "com.android.tools:desugar_jdk_libs:$DESUGAR_JDK_LIBS_VERSION"
}

object Kotlin {
    const val VERSION = "1.4.10"
    private const val COROUTINES_VERSION = "1.3.9"
    private const val DATE_TIME_VERSION = "0.1.0"

    const val GRADLE_PLUGIN = "org.jetbrains.kotlin:kotlin-gradle-plugin:$VERSION"
    const val COROUTINES_CORE = "org.jetbrains.kotlinx:kotlinx-coroutines-core:$COROUTINES_VERSION"
    const val COROUTINES_ANDROID =
        "org.jetbrains.kotlinx:kotlinx-coroutines-android:$COROUTINES_VERSION"
    const val DATE_TIME = "org.jetbrains.kotlinx:kotlinx-datetime:$DATE_TIME_VERSION"
}

object Jetpack {
    private const val ACTIVITY_KTX_VERSION = "1.2.0-alpha08"
    private const val APPCOMPAT_VERSION = "1.3.0-alpha02"
    private const val CORE_KTX_VERSION = "1.5.0-alpha03"
    private const val FRAGMENT_KTX_VERSION = "1.3.0-alpha08"
    private const val PREFERENCE_KTX_VERSION = "1.1.1"

    const val ACTIVITY_KTX = "androidx.activity:activity-ktx:$ACTIVITY_KTX_VERSION"
    const val APPCOMPAT = "androidx.appcompat:appcompat:$APPCOMPAT_VERSION"
    const val CORE_KTX = "androidx.core:core-ktx:$CORE_KTX_VERSION"
    const val FRAGMENT_KTX = "androidx.fragment:fragment-ktx:$FRAGMENT_KTX_VERSION"
    const val PREFERENCE_KTX = "androidx.preference:preference-ktx:$PREFERENCE_KTX_VERSION"
}

object Compose {
    const val VERSION = "1.0.0-alpha03"
    private const val NAVIGATION_VERSION = "0.1.0-SNAPSHOT"
    const val KOTLIN_COMPILER_VERSION = Kotlin.VERSION

    const val RUNTIME = "androidx.compose.runtime:runtime:$VERSION"
    const val FOUNDATION = "androidx.compose.foundation:foundation:$VERSION"
    const val FOUNDATION_LAYOUT = "androidx.compose.foundation:foundation-layout:$VERSION"
    const val MATERIAL = "androidx.compose.material:material:$VERSION"
    const val MATERIAL_ICONS_EXTENDED = "androidx.compose.material:material-icons-extended:$VERSION"
    const val UI_UTIL = "androidx.compose.ui:ui-util:$VERSION"
    const val UI_TOOLING = "androidx.ui:ui-tooling:$VERSION"
    const val NAVIGATION = "androidx.compose.navigation:navigation:$NAVIGATION_VERSION"
}

object Lifecycle {
    private const val VERSION = "2.3.0-alpha07"

    const val SERVICE = "androidx.lifecycle:lifecycle-service:$VERSION"
    const val RUNTIME_KTX = "androidx.lifecycle:lifecycle-runtime-ktx:$VERSION"
    const val LIVEDATA_KTX = "androidx.lifecycle:lifecycle-livedata-ktx:$VERSION"
    const val VIEWMODEL_KTX = "androidx.lifecycle:lifecycle-viewmodel-ktx:$VERSION"
    const val COMMON_JAVA8 = "androidx.lifecycle:lifecycle-common-java8:$VERSION"
}

object WorkManager {
    private const val VERSION = "2.5.0-alpha02"

    const val WORK_RUNTIME_KTX = "androidx.work:work-runtime-ktx:$VERSION"
}

object DaggerHilt {
    private const val VERSION = "2.29.1-alpha"

    const val ANDROID = "com.google.dagger:hilt-android:$VERSION"
    const val COMPILER = "com.google.dagger:hilt-compiler:$VERSION"
    const val ANDROID_GRADLE_PLUGIN = "com.google.dagger:hilt-android-gradle-plugin:$VERSION"
}

object AndroidXHilt {
    private const val VERSION = "1.0.0-alpha02"

    const val COMMON = "androidx.hilt:hilt-common:$VERSION"
    const val COMPILER = "androidx.hilt:hilt-compiler:$VERSION"
    const val WORK = "androidx.hilt:hilt-work:$VERSION"
    const val LIFECYCLE_VIEWMODEL = "androidx.hilt:hilt-lifecycle-viewmodel:$VERSION"
}

object Timber {
    private const val VERSION = "4.7.1"

    const val TIMBER = "com.jakewharton.timber:timber:$VERSION"
}

object LibSU {
    private const val VERSION = "3.0.2"

    const val CORE = "com.github.topjohnwu.libsu:core:$VERSION"
    const val IO = "com.github.topjohnwu.libsu:io:$VERSION"
}

object SQLDelight {
    private const val VERSION = "1.4.3"

    const val GRADLE_PLUGIN = "com.squareup.sqldelight:gradle-plugin:$VERSION"
    const val ANDROID_DRIVER = "com.squareup.sqldelight:android-driver:$VERSION"
    const val COROUTINES_EXTENSIONS = "com.squareup.sqldelight:coroutines-extensions-jvm:$VERSION"
}

object RustAndroidGradle {
    private const val VERSION = "0.8.3"

    const val GRADLE_PLUGIN = "gradle.plugin.org.mozilla.rust-android-gradle:plugin:$VERSION"
}
