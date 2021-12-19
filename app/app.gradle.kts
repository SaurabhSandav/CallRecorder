plugins {
    id(libs.plugins.android.application.get().pluginId)
    id(libs.plugins.kotlin.android.get().pluginId)
    id(libs.plugins.kotlin.kapt.get().pluginId)
    id(libs.plugins.hilt.android.get().pluginId)
}

android {

    compileSdkVersion(30)
    buildToolsVersion = "30.0.2"

    defaultConfig {

        minSdkVersion(29)
        targetSdkVersion(30)

        versionCode = 1
        versionName = "1.0"

        applicationId = "com.redridgeapps.callrecorder"
    }

    compileOptions {

        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8

        isCoreLibraryDesugaringEnabled = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.compose.get()
    }

    buildFeatures.compose = true

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    ndkVersion = "21.3.6528147"
}

dependencies {

    coreLibraryDesugaring(libs.desugarjdklibs)

    implementation(project(":common"))
    implementation(project(":prefs"))
    implementation(project(":call_utils"))
    implementation(project(":wav_utils"))

    // Jetpack
    implementation(libs.jetpack.activity)
    implementation(libs.jetpack.appcompat)
    implementation(libs.jetpack.core)
    implementation(libs.jetpack.fragment)
    implementation(libs.jetpack.preference)

    // Compose
    implementation(libs.compose.runtime)
    implementation(libs.compose.foundation)
    implementation(libs.compose.material)
    implementation(libs.compose.material.icons.extended)
    implementation(libs.jetpack.navigation.compose)

    // Lifecycle, ViewModel and LiveData
    implementation(libs.jetpack.lifecycle.service)
    implementation(libs.jetpack.lifecycle.viewmodel)

    // WorkManager
    implementation(libs.jetpack.work.runtime)

    // Dagger Hilt
    implementation(libs.dagger.hilt.android)
    kapt(libs.dagger.hilt.compiler)

    // AndroidX Hilt
    implementation(libs.jetpack.hilt.common)
    implementation(libs.jetpack.hilt.work)
    implementation(libs.jetpack.hilt.lifecycle.viewmodel)
    kapt(libs.jetpack.hilt.compiler)

    // Timber
    implementation(libs.timber)
}
