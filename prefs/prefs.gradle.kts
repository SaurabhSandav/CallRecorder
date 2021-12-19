plugins {
    id(libs.plugins.android.library.get().pluginId)
    id(libs.plugins.kotlin.android.get().pluginId)
    id(libs.plugins.kotlin.kapt.get().pluginId)
    id(libs.plugins.wire.get().pluginId)
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
