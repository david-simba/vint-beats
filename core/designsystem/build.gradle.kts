plugins {
    id("vint.android.library")
    id("vint.android.compose")
}

android {
    namespace = "com.davidsimba.vintbeats.core.designsystem"
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.compose.material.icons)
    implementation(libs.coil.compose)
    implementation(libs.androidx.palette)
    api(project(":core:model"))
}
