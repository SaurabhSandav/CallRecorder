plugins {
    id(libs.plugins.android.library.get().pluginId)
    id(libs.plugins.kotlin.android.get().pluginId)
    id(libs.plugins.kotlin.kapt.get().pluginId)
}

android {

    compileSdk = 31
    buildToolsVersion = "32.0.0"

    defaultConfig {

        minSdk = 29
        targetSdk = 31
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11

        isCoreLibraryDesugaringEnabled = true
    }

    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {

    coreLibraryDesugaring(libs.desugarJdkLibs)

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
