@file:Suppress("unused")

object GoogleMaven {
    const val SNAPSHOT_BUILD_ID = "6634459"

    const val SUPPORT_REPO_ENABLED = false
    const val SUPPORT_REPO_URL =
        "https://androidx.dev/snapshots/builds/$SNAPSHOT_BUILD_ID/artifacts/repository"

    const val UI_REPO_ENABLED = true
    const val UI_REPO_URL =
        "https://androidx.dev/snapshots/builds/$SNAPSHOT_BUILD_ID/artifacts/ui/repository"
}

object AndroidTools {
    private const val GRADLE_PLUGIN_VERSION = "4.2.0-alpha09"
    private const val DESUGAR_JDK_LIBS_VERSION = "1.0.9"

    const val GRADLE_PLUGIN = "com.android.tools.build:gradle:$GRADLE_PLUGIN_VERSION"
    const val DESUGAR_JDK_LIBS = "com.android.tools:desugar_jdk_libs:$DESUGAR_JDK_LIBS_VERSION"
}

object Kotlin {
    const val VERSION = "1.4.0"
    private const val COROUTINES_VERSION = "1.3.9"

    const val GRADLE_PLUGIN = "org.jetbrains.kotlin:kotlin-gradle-plugin:$VERSION"
    const val COROUTINES_CORE = "org.jetbrains.kotlinx:kotlinx-coroutines-core:$COROUTINES_VERSION"
    const val COROUTINES_ANDROID =
        "org.jetbrains.kotlinx:kotlinx-coroutines-android:$COROUTINES_VERSION"
}

object Jetpack {
    private const val ACTIVITY_KTX_VERSION = "1.2.0-alpha08"
    private const val APPCOMPAT_VERSION = "1.3.0-alpha02"
    private const val CORE_KTX_VERSION = "1.5.0-alpha02"
    private const val FRAGMENT_KTX_VERSION = "1.3.0-alpha08"
    private const val PREFERENCE_KTX_VERSION = "1.1.1"

    const val ACTIVITY_KTX = "androidx.activity:activity-ktx:$ACTIVITY_KTX_VERSION"
    const val APPCOMPAT = "androidx.appcompat:appcompat:$APPCOMPAT_VERSION"
    const val CORE_KTX = "androidx.core:core-ktx:$CORE_KTX_VERSION"
    const val FRAGMENT_KTX = "androidx.fragment:fragment-ktx:$FRAGMENT_KTX_VERSION"
    const val PREFERENCE_KTX = "androidx.preference:preference-ktx:$PREFERENCE_KTX_VERSION"
}

object Compose {
    const val VERSION = "1.0.0-alpha01"
    const val KOTLIN_COMPILER_VERSION = Kotlin.VERSION

    const val RUNTIME = "androidx.compose.runtime:runtime:$VERSION"
    const val FOUNDATION = "androidx.compose.foundation:foundation:$VERSION"
    const val FOUNDATION_LAYOUT = "androidx.compose.foundation:foundation-layout:$VERSION"
    const val MATERIAL = "androidx.compose.material:material:$VERSION"
    const val MATERIAL_ICONS_EXTENDED = "androidx.compose.material:material-icons-extended:$VERSION"
    const val UI_UTIL = "androidx.compose.ui:ui-util:$VERSION"
    const val UI_TOOLING = "androidx.ui:ui-tooling:$VERSION"
}

object ComposeNavigation {
    private const val VERSION = "0.3.7"

    const val CORE = "com.github.mvarnagiris.compose-navigation:core:$VERSION"
    const val NAVIGATION = "com.github.mvarnagiris.compose-navigation:navigation:$VERSION"
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
    private const val VERSION = "2.5.0-alpha01"

    const val WORK_RUNTIME_KTX = "androidx.work:work-runtime-ktx:$VERSION"
}

object JavaXInject {
    private const val VERSION = "1"

    const val JAVAX_INJECT = "javax.inject:javax.inject:$VERSION"
}

object Dagger {
    private const val HILT_VERSION = "2.28.3-alpha"

    const val HILT_ANDROID = "com.google.dagger:hilt-android:$HILT_VERSION"
    const val HILT_ANDROID_COMPILER = "com.google.dagger:hilt-android-compiler:$HILT_VERSION"
    const val HILT_ANDROID_GRADLE_PLUGIN =
        "com.google.dagger:hilt-android-gradle-plugin:$HILT_VERSION"
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
    private const val VERSION = "1.4.2"

    const val GRADLE_PLUGIN = "com.squareup.sqldelight:gradle-plugin:$VERSION"
    const val ANDROID_DRIVER = "com.squareup.sqldelight:android-driver:$VERSION"
    const val COROUTINES_EXTENSIONS = "com.squareup.sqldelight:coroutines-extensions-jvm:$VERSION"
}

object RustAndroidGradle {
    private const val ANDROID_WAV_TO_MP3_VERSION = "0.8.3"

    const val GRADLE_PLUGIN =
        "gradle.plugin.org.mozilla.rust-android-gradle:plugin:$ANDROID_WAV_TO_MP3_VERSION"
}
