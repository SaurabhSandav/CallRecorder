plugins {
    id("com.android.library")
    id("kotlin-android")
    id("kotlin-kapt")
}

android {

    compileSdkVersion(30)
    buildToolsVersion = "30.0.2"

    defaultConfig {

        minSdkVersion(29)
        targetSdkVersion(30)
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8

        isCoreLibraryDesugaringEnabled = true
    }
}

dependencies {

    coreLibraryDesugaring(AndroidTools.DESUGAR_JDK_LIBS)

    // KotlinX
    api(Kotlin.COROUTINES_ANDROID)
    api(Kotlin.DATE_TIME)

    // Jetpack
    implementation(Jetpack.ACTIVITY_KTX)

    // Dagger Hilt
    implementation(DaggerHilt.ANDROID)
    kapt(DaggerHilt.COMPILER)

    // AndroidX Hilt
    implementation(AndroidXHilt.LIFECYCLE_VIEWMODEL)

    // SQLDelight
    implementation(SQLDelight.ANDROID_DRIVER)

    // LibSU
    implementation(LibSU.CORE)
    implementation(LibSU.IO)

    // Timber
    implementation(Timber.TIMBER)
}
