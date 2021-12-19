plugins {
    id(libs.plugins.android.library.get().pluginId)
    id(libs.plugins.kotlin.android.get().pluginId)
    id(libs.plugins.kotlin.kapt.get().pluginId)
    id(libs.plugins.sqldelight.get().pluginId)
    id(libs.plugins.hilt.android.get().pluginId)
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
            "src/main/java",
            "src/main/sqldelight/"
        )
    }
}

dependencies {

    implementation(projects.common)
    implementation(projects.prefs)
    implementation(projects.wavUtils)
    implementation(projects.mp3Encoder)

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
