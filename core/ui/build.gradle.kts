plugins {
    id("vint.android.library")
    id("vint.android.compose")
    id("vint.android.hilt")
}

android {
    namespace = "com.davidsimba.vintbeats.core.ui"
}

dependencies {
    api(project(":core:model"))
    api(project(":core:data"))
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.hilt.navigation.compose)
    implementation(libs.kotlinx.coroutines.android)
}
