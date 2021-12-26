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

rootProject.name = "CallRecorder"

listOf(
    "TYPESAFE_PROJECT_ACCESSORS",
    "VERSION_CATALOGS",
).forEach { enableFeaturePreview(it) }

include(
    ":app",
    ":common",
    ":call_utils",
    ":wav_utils",
    ":mp3_encoder",
)

rootProject.children.forEach { subproject ->
    subproject.buildFileName = "${subproject.name}.gradle.kts"
}
