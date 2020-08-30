import com.android.build.gradle.BaseExtension
import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.getByType

@Suppress("UnstableApiUsage", "unused")
class CommonConfigurationPlugin : Plugin<Project> {

    override fun apply(target: Project) {

        val commonConfigurationExtension: CommonConfigurationExtension = target.extensions.create(
            "commonConfiguration", CommonConfigurationExtension::class.java
        )

        target.afterEvaluate {

            target.configureAndroidCommon()

            if (commonConfigurationExtension.compose)
                target.configureCompose()

            if (commonConfigurationExtension.coreLibraryDesugaring)
                target.configureCoreLibraryDesugaring()

            if (commonConfigurationExtension.timber)
                target.configureTimber()
        }
    }

    private fun Project.configureAndroidCommon() = extensions.getByType<BaseExtension>().run {

        compileSdkVersion(30)
        buildToolsVersion = "30.0.0"

        defaultConfig {
            minSdkVersion(29)
            targetSdkVersion(30)
            versionCode = 1
            versionName = "1.0"
        }

        compileOptions {
            sourceCompatibility = JavaVersion.VERSION_1_8
            targetCompatibility = JavaVersion.VERSION_1_8
        }
    }

    private fun Project.configureCompose() = extensions.getByType<BaseExtension>().run {

        buildFeatures.compose = true

        composeOptions {
            kotlinCompilerVersion = Compose.KOTLIN_COMPILER_VERSION
            kotlinCompilerExtensionVersion = Compose.VERSION
        }
    }

    private fun Project.configureCoreLibraryDesugaring() {

        extensions.getByType<BaseExtension>().compileOptions.isCoreLibraryDesugaringEnabled = true

        dependencies.add("coreLibraryDesugaring", AndroidTools.DESUGAR_JDK_LIBS)
    }

    private fun Project.configureTimber() {
        dependencies.add("implementation", Timber.TIMBER)
    }
}
