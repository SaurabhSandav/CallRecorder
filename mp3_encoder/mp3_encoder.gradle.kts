plugins {
    id(libs.plugins.android.library.get().pluginId)
    id(libs.plugins.kotlin.android.get().pluginId)
    id("org.mozilla.rust-android-gradle.rust-android") version "0.9.0"
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

    ndkVersion = "22.1.7171670"
}

dependencies {

    implementation(projects.wavUtils)

    // Timber
    implementation(libs.timber)
}

cargo {
    module = "../lame_wrapper/"
    libname = "lame_wrapper"
    targets = listOf("arm", "x86", "x86_64", "arm64")
}

tasks.whenTaskAdded {
    if (name == "javaPreCompileDebug" || name == "javaPreCompileRelease") {
        dependsOn("cargoBuild")
    }
}
