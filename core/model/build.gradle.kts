plugins {
    id("vint.android.library")
}

android {
    namespace = "com.davidsimba.vintbeats.core.model"
}

dependencies {
    // Solo para la anotación @Immutable de los modelos
    implementation(platform(libs.androidx.compose.bom))
    api(libs.androidx.compose.runtime)
}
