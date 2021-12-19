plugins {
    id(libs.plugins.android.library.get().pluginId)
    id(libs.plugins.kotlin.android.get().pluginId)
//    id("org.mozilla.rust-android-gradle.rust-android") version "0.8.3"
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

    ndkVersion = "21.3.6528147"
}

dependencies {

    implementation(projects.wavUtils)

    // Timber
    implementation(libs.timber)
}

/*cargo {
    module = "../lame_wrapper/"
    libname = "lame_wrapper"
    targets = listOf("arm", "x86", "x86_64", "arm64")
}*/

/*tasks.whenTaskAdded { task ->
    if ((task.name == 'javaPreCompileDebug' || task.name == 'javaPreCompileRelease')) {
        task.dependsOn 'cargoBuild'
    }
}*/
