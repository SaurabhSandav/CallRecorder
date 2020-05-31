@file:Suppress("unused")

object GoogleMaven {
    const val SNAPSHOT_BUILD_ID = "6564799"

    const val SUPPORT_REPO_ENABLED = true
    const val SUPPORT_REPO_URL =
        "https://androidx.dev/snapshots/builds/$SNAPSHOT_BUILD_ID/artifacts/repository"

    const val UI_REPO_ENABLED = true
    const val UI_REPO_URL =
        "https://androidx.dev/snapshots/builds/$SNAPSHOT_BUILD_ID/artifacts/ui/repository"
}

object AndroidTools {
    private const val GRADLE_PLUGIN_VERSION = "4.1.0-alpha10"
    private const val DESUGAR_JDK_LIBS_VERSION = "1.0.5"

    const val GRADLE_PLUGIN = "com.android.tools.build:gradle:$GRADLE_PLUGIN_VERSION"
    const val DESUGAR_JDK_LIBS = "com.android.tools:desugar_jdk_libs:$DESUGAR_JDK_LIBS_VERSION"
}

object Kotlin {
    private const val VERSION = "1.3.72"
    private const val COROUTINES_VERSION = "1.3.7"

    const val GRADLE_PLUGIN = "org.jetbrains.kotlin:kotlin-gradle-plugin:$VERSION"
    const val STDLIB = "org.jetbrains.kotlin:kotlin-stdlib-jdk7:$VERSION"
    const val COROUTINES_CORE = "org.jetbrains.kotlinx:kotlinx-coroutines-core:$COROUTINES_VERSION"
    const val COROUTINES_ANDROID =
        "org.jetbrains.kotlinx:kotlinx-coroutines-android:$COROUTINES_VERSION"
}

object Jetpack {
    private const val ACTIVITY_KTX_VERSION = "1.2.0-alpha05"
    private const val APPCOMPAT_VERSION = "1.3.0-alpha01"
    private const val CORE_KTX_VERSION = "1.4.0-alpha01"
    private const val PREFERENCE_KTX_VERSION = "1.1.1"

    const val ACTIVITY_KTX = "androidx.activity:activity-ktx:$ACTIVITY_KTX_VERSION"
    const val APPCOMPAT = "androidx.appcompat:appcompat:$APPCOMPAT_VERSION"
    const val CORE_KTX = "androidx.core:core-ktx:$CORE_KTX_VERSION"
    const val PREFERENCE_KTX = "androidx.preference:preference-ktx:$PREFERENCE_KTX_VERSION"
}

object Compose {
    private const val VERSION = "0.1.0-SNAPSHOT"

    const val COMPOSE_RUNTIME = "androidx.compose:compose-runtime:$VERSION"
    const val UI_FOUNDATION = "androidx.ui:ui-foundation:$VERSION"
    const val UI_LAYOUT = "androidx.ui:ui-layout:$VERSION"
    const val UI_MATERIAL = "androidx.ui:ui-material:$VERSION"
    const val UI_MATERIAL_ICONS_EXTENDED = "androidx.ui:ui-material-icons-extended:$VERSION"
    const val UI_UTIL = "androidx.ui:ui-util:$VERSION"
    const val UI_TOOLING = "androidx.ui:ui-tooling:$VERSION"
}

object ComposeNavigation {
    private const val VERSION = GoogleMaven.SNAPSHOT_BUILD_ID

    const val CORE = "com.github.mvarnagiris.compose-navigation:core:$VERSION"
    const val NAVIGATION = "com.github.mvarnagiris.compose-navigation:navigation:$VERSION"
}

object Lifecycle {
    private const val VERSION = "2.3.0-alpha03"

    const val SERVICE = "androidx.lifecycle:lifecycle-service:$VERSION"
    const val LIVEDATA_KTX = "androidx.lifecycle:lifecycle-livedata-ktx:$VERSION"
    const val VIEWMODEL_KTX = "androidx.lifecycle:lifecycle-viewmodel-ktx:$VERSION"
    const val COMMON_JAVA8 = "androidx.lifecycle:lifecycle-common-java8:$VERSION"
}

object Dagger {
    private const val VERSION = "2.28"
    private const val HILT_VERSION = "2.28-alpha"

    const val DAGGER = "com.google.dagger:dagger:$VERSION"
    const val COMPILER = "com.google.dagger:dagger-compiler:$VERSION"

    const val HILT_ANDROID = "com.google.dagger:hilt-android:$HILT_VERSION"
    const val HILT_ANDROID_COMPILER = "com.google.dagger:hilt-android-compiler:$HILT_VERSION"
    const val HILT_ANDROID_GRADLE_PLUGIN =
        "com.google.dagger:hilt-android-gradle-plugin:$HILT_VERSION"
}

object Hilt {
    private const val VERSION = "1.0.0-SNAPSHOT"

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
    private const val VERSION = "2.5.1"

    const val CORE = "com.github.topjohnwu.libsu:core:$VERSION"
    const val IO = "com.github.topjohnwu.libsu:io:$VERSION"
}

object SQLDelight {
    private const val VERSION = "1.3.0"

    const val GRADLE_PLUGIN = "com.squareup.sqldelight:gradle-plugin:$VERSION"
    const val ANDROID_DRIVER = "com.squareup.sqldelight:android-driver:$VERSION"
    const val COROUTINES_EXTENSIONS = "com.squareup.sqldelight:coroutines-extensions-jvm:$VERSION"
}

object RustAndroidGradle {
    private const val ANDROID_WAV_TO_MP3_VERSION = "0.8.3"

    const val GRADLE_PLUGIN =
        "gradle.plugin.org.mozilla.rust-android-gradle:plugin:$ANDROID_WAV_TO_MP3_VERSION"
}
