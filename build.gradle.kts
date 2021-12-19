// Top-level build file where you can add configuration options common to all sub-projects/modules.

import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    alias(libs.plugins.gradle.versions.checker)
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.sqldelight) apply false
    alias(libs.plugins.hilt.android) apply false
    alias(libs.plugins.wire) apply false
}

allprojects {

    repositories {
        mavenLocal()
        google()
        mavenCentral()
        maven("https://jitpack.io") // For libsu
        maven("https://kotlin.bintray.com/kotlinx/") // For kotlinx-datetime
    }

    tasks.withType<KotlinCompile>().configureEach {
        kotlinOptions {
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
