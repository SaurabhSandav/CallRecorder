
pluginManagement {

    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
    }

    resolutionStrategy {
        eachPlugin {
            if (requested.id.id == "dagger.hilt.android.plugin") {
                useModule("com.google.dagger:hilt-android-gradle-plugin:${requested.version}")
            } else if (requested.id.id == "com.squareup.wire") {
                useModule("com.squareup.wire:wire-gradle-plugin:${requested.version}")
            }
        }
    }
}

rootProject.name = "Call Recorder"

listOf(
    "VERSION_CATALOGS",
).forEach { enableFeaturePreview(it) }

include(":app")
include(":common")
include(":prefs")
include(":call_utils")
include(":wav_utils")
include(":mp3_encoder")

rootProject.children.forEach { subproject ->
    subproject.buildFileName = "${subproject.name}.gradle.kts"
}
