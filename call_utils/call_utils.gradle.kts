plugins {
    id("com.android.library")
    id("kotlin-android")
    id("kotlin-kapt")
    id("com.squareup.sqldelight")
    id("dagger.hilt.android.plugin")
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
    implementation(Jetpack.CORE_KTX)

    // Lifecycle
    implementation(Lifecycle.SERVICE)
    implementation(Lifecycle.RUNTIME_KTX)

    // WorkManager
    implementation(WorkManager.WORK_RUNTIME_KTX)

    // Dagger Hilt
    implementation(DaggerHilt.ANDROID)
    kapt(DaggerHilt.COMPILER)

    // AndroidX Hilt
    implementation(AndroidXHilt.COMMON)
    implementation(AndroidXHilt.WORK)
    kapt(AndroidXHilt.COMPILER)

    // SQLDelight
    implementation(SQLDelight.ANDROID_DRIVER)
    implementation(SQLDelight.COROUTINES_EXTENSIONS)
}

sqldelight {

    database("CallRecordingDB") {
        packageName = "com.redridgeapps.callutils.db"
        sourceFolders = listOf("sqldelight")
        schemaOutputDirectory = file("src/main/sqldelight/schema")
    }
}
