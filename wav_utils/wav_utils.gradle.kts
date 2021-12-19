plugins {
    id(libs.plugins.kotlin.multiplatform.get().pluginId)
}

kotlin {

    explicitApi()

    jvm {
        compilations.all {
            kotlinOptions.jvmTarget = "11"
        }
    }

    sourceSets {

        all {

            languageSettings {

                progressiveMode = true

                optIn("kotlin.time.ExperimentalTime")
            }
        }

        named("commonMain") {
        }
    }
}
