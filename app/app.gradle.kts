plugins {
    id("com.android.application")
    id("kotlin-android")
    id("kotlin-kapt")
    id("dagger.hilt.android.plugin")
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
        kotlinCompilerVersion = Compose.KOTLIN_COMPILER_VERSION
        kotlinCompilerExtensionVersion = Compose.VERSION
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

    coreLibraryDesugaring(AndroidTools.DESUGAR_JDK_LIBS)

    implementation(project(":common"))
    implementation(project(":prefs"))
    implementation(project(":call_utils"))
    implementation(project(":wav_utils"))

    // Jetpack
    implementation(Jetpack.ACTIVITY_KTX)
    implementation(Jetpack.APPCOMPAT)
    implementation(Jetpack.CORE_KTX)
    implementation(Jetpack.FRAGMENT_KTX)
    implementation(Jetpack.PREFERENCE_KTX)

    // Compose
    implementation(Compose.RUNTIME)
    implementation(Compose.FOUNDATION)
    implementation(Compose.MATERIAL)
    implementation(Compose.MATERIAL_ICONS_EXTENDED)
    implementation(Compose.NAVIGATION)

    // Lifecycle, ViewModel and LiveData
    implementation(Lifecycle.SERVICE)
    implementation(Lifecycle.VIEWMODEL_KTX)

    // WorkManager
    implementation(WorkManager.WORK_RUNTIME_KTX)

    // Dagger Hilt
    implementation(DaggerHilt.ANDROID)
    kapt(DaggerHilt.COMPILER)

    // AndroidX Hilt
    implementation(AndroidXHilt.COMMON)
    implementation(AndroidXHilt.WORK)
    implementation(AndroidXHilt.LIFECYCLE_VIEWMODEL)
    kapt(AndroidXHilt.COMPILER)

    // Timber
    implementation(Timber.TIMBER)
}
