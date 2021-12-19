plugins {
    id(libs.plugins.android.library.get().pluginId)
    id(libs.plugins.kotlin.android.get().pluginId)
    id(libs.plugins.kotlin.kapt.get().pluginId)
    id(libs.plugins.wire.get().pluginId)
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
    }

    kotlinOptions {
        jvmTarget = "11"
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
    implementation(libs.kotlinx.coroutines.android)

    // Jetpack
    implementation(libs.jetpack.core)

    // DataStore
    api(libs.jetpack.datastore.core)

    // Dagger Hilt
    implementation(libs.dagger.hilt.android)
    kapt(libs.dagger.hilt.compiler)

    // Wire
    api(libs.wire)
}

wire {
    kotlin {
    }
}
