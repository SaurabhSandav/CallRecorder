// Top-level build file where you can add configuration options common to all sub-projects/modules.

import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("dependencies")
    id("com.github.ben-manes.versions") version "0.33.0"
    id("com.android.application") version "7.0.0" apply false
    id("org.jetbrains.kotlin.android") version "1.4.10" apply false
    id("com.squareup.sqldelight") version "1.4.4" apply false
    id("dagger.hilt.android.plugin") version "2.29.1-alpha" apply false
    id("com.squareup.wire") version "3.4.0" apply false
}

allprojects {

    repositories {
        mavenLocal()
        google()
        mavenCentral()
        maven("https://jitpack.io") // For libsu
        maven("https://kotlin.bintray.com/kotlinx/") // For kotlinx-datetime

        if (GoogleMaven.ANDROIDX_REPO_ENABLED) {
            maven(GoogleMaven.ANDROIDX_REPO_URL)
        }
    }

    tasks.withType<KotlinCompile>().configureEach {
        kotlinOptions {
            jvmTarget = "1.8"
            freeCompilerArgs += listOf(
                "-progressive",
                "-Xinline-classes",
                "-Xskip-prerelease-check",
                "-Xallow-jvm-ir-dependencies",
                "-Xopt-in=kotlin.ExperimentalStdlibApi",
                "-Xopt-in=kotlin.time.ExperimentalTime",
                "-Xopt-in=kotlinx.coroutines.ExperimentalCoroutinesApi",
                "-Xopt-in=kotlinx.coroutines.ObsoleteCoroutinesApi",
                "-Xopt-in=kotlinx.coroutines.FlowPreview",
                "-Xopt-in=androidx.compose.runtime.ExperimentalComposeApi",
                "-Xopt-in=androidx.compose.foundation.layout.ExperimentalLayout",
                "-Xopt-in=androidx.compose.animation.ExperimentalAnimationApi",
                "-Xopt-in=androidx.compose.material.ExperimentalMaterialApi",
            )
        }
    }
}

task<Delete>("clean") {
    delete(rootProject.buildDir)
}
