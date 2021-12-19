plugins {
    id(libs.plugins.android.library.get().pluginId)
    id(libs.plugins.kotlin.android.get().pluginId)
    id(libs.plugins.kotlin.kapt.get().pluginId)
    id(libs.plugins.sqldelight.get().pluginId)
    id(libs.plugins.hilt.android.get().pluginId)
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
            "src/main/java",
            "src/main/sqldelight/"
        )
    }
}

dependencies {

    implementation(project(":common"))
    implementation(project(":prefs"))
    implementation(project(":wav_utils"))
    implementation(project(":mp3_encoder"))

    // Jetpack
    implementation(libs.jetpack.core)

    // Lifecycle
    implementation(libs.jetpack.lifecycle.service)
    implementation(libs.jetpack.lifecycle.runtime)

    // WorkManager
    implementation(libs.jetpack.work.runtime)

    // Dagger Hilt
    implementation(libs.dagger.hilt.android)
    kapt(libs.dagger.hilt.compiler)

    // AndroidX Hilt
    implementation(libs.jetpack.hilt.common)
    implementation(libs.jetpack.hilt.work)
    kapt(libs.jetpack.hilt.compiler)

    // SQLDelight
    implementation(libs.sqldelight.android.driver)
    implementation(libs.sqldelight.coroutines.extensions.jvm)
}

sqldelight {

    database("CallRecordingDB") {
        packageName = "com.redridgeapps.callutils.db"
        sourceFolders = listOf("sqldelight")
        schemaOutputDirectory = file("src/main/sqldelight/schema")
    }
}
