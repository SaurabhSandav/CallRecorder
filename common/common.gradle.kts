plugins {
    id(libs.plugins.android.library.get().pluginId)
    id(libs.plugins.kotlin.android.get().pluginId)
    id(libs.plugins.kotlin.kapt.get().pluginId)
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

    coreLibraryDesugaring(libs.desugarjdklibs)

    // KotlinX
    implementation(libs.kotlinx.coroutines.android)
    api(libs.kotlinx.datetime)

    // Jetpack
    implementation(libs.jetpack.activity)

    // Dagger Hilt
    implementation(libs.dagger.hilt.android)
    kapt(libs.dagger.hilt.compiler)

    // AndroidX Hilt
    implementation(libs.jetpack.hilt.lifecycle.viewmodel)

    // SQLDelight
    implementation(libs.sqldelight.android.driver)

    // LibSU
    implementation(libs.libsu.core)
    implementation(libs.libsu.io)

    // Timber
    implementation(libs.timber)
}
