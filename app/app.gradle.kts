plugins {
    id(libs.plugins.android.application.get().pluginId)
    id(libs.plugins.kotlin.android.get().pluginId)
    id(libs.plugins.kotlin.kapt.get().pluginId)
    id(libs.plugins.hilt.android.get().pluginId)
}

android {

    compileSdk = 31
    buildToolsVersion = "32.0.0"

    defaultConfig {

        minSdk = 29
        targetSdk = 31

        versionCode = 1
        versionName = "1.0"

        applicationId = "com.redridgeapps.callrecorder"
    }

    compileOptions {

        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11

        isCoreLibraryDesugaringEnabled = true
    }

    kotlinOptions {
        jvmTarget = "11"

        freeCompilerArgs += listOf(
            "-opt-in=androidx.compose.runtime.InternalComposeApi",
            "-opt-in=androidx.compose.foundation.ExperimentalFoundationApi",
            "-P",
            "plugin:androidx.compose.compiler.plugins.kotlin:suppressKotlinVersionCompatibilityCheck=true",
        )
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

    coreLibraryDesugaring(libs.desugarJdkLibs)

    implementation(projects.common)
    implementation(projects.callUtils)
    implementation(projects.wavUtils)

    // Jetpack
    implementation(libs.jetpack.activity)
    implementation(libs.jetpack.appcompat)
    implementation(libs.jetpack.core)
    implementation(libs.jetpack.fragment)
    implementation(libs.jetpack.preference)

    // DataStore
    implementation(libs.jetpack.datastore)
    implementation(libs.jetpack.datastore.core)
    implementation(libs.jetpack.datastore.preferences)

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

    // Jetpack Hilt
    implementation(libs.jetpack.hilt.common)
    implementation(libs.jetpack.hilt.navigation.compose)
    implementation(libs.jetpack.hilt.work)
    kapt(libs.jetpack.hilt.compiler)

    // Multiplatform Settings
    implementation(libs.multiplatform.settings.core)
    implementation(libs.multiplatform.settings.coroutines)
    implementation(libs.multiplatform.settings.datastore)

    // Timber
    implementation(libs.timber)
}
