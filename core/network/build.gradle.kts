plugins {
    id("vint.android.library")
    id("vint.android.hilt")
}

android {
    namespace = "com.davidsimba.vintbeats.core.network"
}

dependencies {
    api(project(":core:model"))
    implementation(libs.okhttp)
    implementation(libs.okhttp.logging)
    implementation(libs.gson)
    implementation(libs.coil.compose)
    implementation(libs.kotlinx.coroutines.android)
}
