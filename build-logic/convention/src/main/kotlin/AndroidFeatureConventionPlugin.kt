import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

/**
 * Módulo de feature: library + Compose + Hilt + lo mínimo que toda pantalla necesita.
 * Un feature solo depende de :core:*, nunca de otro feature.
 */
class AndroidFeatureConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        pluginManager.apply("vint.android.library")
        pluginManager.apply("vint.android.compose")
        pluginManager.apply("vint.android.hilt")

        dependencies {
            add("implementation", libs.findLibrary("androidx-core-ktx").get())
            add("implementation", libs.findLibrary("androidx-lifecycle-runtime-ktx").get())
            add("implementation", libs.findLibrary("androidx-hilt-navigation-compose").get())
        }
    }
}
