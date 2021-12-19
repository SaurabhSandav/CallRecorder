plugins {
    id("com.android.library")
    id("kotlin-android")
    id("kotlin-kapt")
    id("com.squareup.wire")
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
    }

    sourceSets {
        getByName("main").java.srcDirs(
            "$buildDir/generated/source/wire",
            "src/main/proto/"
        )
    }
}

dependencies {

    // Kotlin
    implementation(Kotlin.COROUTINES_ANDROID)

    // Jetpack
    implementation(Jetpack.CORE_KTX)

    // DataStore
    api(DataStore.CORE)

    // Dagger Hilt
    implementation(DaggerHilt.ANDROID)
    kapt(DaggerHilt.COMPILER)

    // Wire
    api(Wire.RUNTIME)
}

wire {
    kotlin {
    }
}
