plugins {
    id(libs.plugins.android.library.get().pluginId)
    id(libs.plugins.kotlin.android.get().pluginId)
//    id("org.mozilla.rust-android-gradle.rust-android") version "0.8.3"
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

    ndkVersion = "21.3.6528147"
}

dependencies {

    implementation(project(":wav_utils"))

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
