@file:Suppress("unused")

object AndroidTools {
    private const val GRADLE_PLUGIN_VERSION = "4.1.0-alpha01"
    private const val DESUGAR_JDK_LIBS_VERSION = "1.0.4"

    const val GRADLE_PLUGIN = "com.android.tools.build:gradle:$GRADLE_PLUGIN_VERSION"
    const val DESUGAR_JDK_LIBS = "com.android.tools:desugar_jdk_libs:$DESUGAR_JDK_LIBS_VERSION"
}

object Kotlin {
    private const val VERSION = "1.3.61"
    private const val COROUTINES_VERSION = "1.3.3"

    const val GRADLE_PLUGIN = "org.jetbrains.kotlin:kotlin-gradle-plugin:$VERSION"
    const val STDLIB = "org.jetbrains.kotlin:kotlin-stdlib-jdk7:$VERSION"
    const val COROUTINES_ANDROID =
        "org.jetbrains.kotlinx:kotlinx-coroutines-android:$COROUTINES_VERSION"

    const val enableKotlinEAP = false
    const val enableKotlinXEAP = false
}

object JUnit {
    private const val JUNIT_VERSION = "4.13"

    const val JUNIT = "junit:junit:$JUNIT_VERSION"
}

object AndroidXTest {
    private const val CORE_KTX_VERSION = "1.2.0"
    private const val RUNNER_VERSION = "1.2.0"
    private const val EXT_JUNIT_KTX_VERSION = "1.1.1"

    const val CORE_KTX = "androidx.test:core-ktx:$CORE_KTX_VERSION"
    const val RUNNER = "androidx.test:runner:$RUNNER_VERSION"
    const val EXT_JUNIT_KTX = "androidx.test.ext:junit-ktx:$EXT_JUNIT_KTX_VERSION"
}

object Espresso {
    private const val VERSION = "3.2.0"

    const val CORE = "androidx.test.espresso:espresso-core:$VERSION"
}

object Material {
    private const val VERSION = "1.2.0-alpha05"

    const val MATERIAL = "com.google.android.material:material:$VERSION"
}

object Jetpack {
    private const val ACTIVITY_KTX_VERSION = "1.1.0"
    private const val APPCOMPAT_VERSION = "1.2.0-alpha02"
    private const val CORE_KTX_VERSION = "1.3.0-alpha01"
    private const val FRAGMENT_KTX_VERSION = "1.2.2"
    private const val PREFERENCE_KTX_VERSION = "1.1.0"

    const val ACTIVITY_KTX = "androidx.activity:activity-ktx:$ACTIVITY_KTX_VERSION"
    const val APPCOMPAT = "androidx.appcompat:appcompat:$APPCOMPAT_VERSION"
    const val CORE_KTX = "androidx.core:core-ktx:$CORE_KTX_VERSION"
    const val FRAGMENT_KTX = "androidx.fragment:fragment-ktx:$FRAGMENT_KTX_VERSION"
    const val PREFERENCE_KTX = "androidx.preference:preference-ktx:$PREFERENCE_KTX_VERSION"
}

object Compose {
    private const val VERSION = "0.1.0-dev05"

    const val COMPOSE_RUNTIME = "androidx.compose:compose-runtime:$VERSION"
    const val UI_FOUNDATION = "androidx.ui:ui-foundation:$VERSION"
    const val UI_FRAMEWORK = "androidx.ui:ui-framework:$VERSION"
    const val UI_LAYOUT = "androidx.ui:ui-layout:$VERSION"
    const val UI_MATERIAL = "androidx.ui:ui-material:$VERSION"
    const val UI_TOOLING = "androidx.ui:ui-tooling:$VERSION"

    const val fromAOSP = false
}

object Lifecycle {
    private const val VERSION = "2.2.0"

    const val SERVICE = "androidx.lifecycle:lifecycle-service:$VERSION"
    const val LIVEDATA_KTX = "androidx.lifecycle:lifecycle-livedata-ktx:$VERSION"
    const val VIEWMODEL_KTX = "androidx.lifecycle:lifecycle-viewmodel-ktx:$VERSION"
    const val COMMON_JAVA8 = "androidx.lifecycle:lifecycle-common-java8:$VERSION"
}

object Dagger {
    private const val VERSION = "2.26"

    const val DAGGER = "com.google.dagger:dagger:$VERSION"
    const val ANDROID_SUPPORT = "com.google.dagger:dagger-android-support:$VERSION"
    const val COMPILER = "com.google.dagger:dagger-compiler:$VERSION"
    const val ANDROID_PROCESSOR = "com.google.dagger:dagger-android-processor:$VERSION"
}

object JavaX {
    private const val VERSION = "1"

    const val INJECT = "javax.inject:javax.inject:$VERSION"
}

object LeakCanary {
    private const val VERSION = "2.1"

    const val ANDROID = "com.squareup.leakcanary:leakcanary-android:$VERSION"
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
    private const val VERSION = "1.2.2"

    const val GRADLE_PLUGIN = "com.squareup.sqldelight:gradle-plugin:$VERSION"
    const val ANDROID_DRIVER = "com.squareup.sqldelight:android-driver:$VERSION"
    const val COROUTINES_EXTENSIONS = "com.squareup.sqldelight:coroutines-extensions-jvm:$VERSION"
}
