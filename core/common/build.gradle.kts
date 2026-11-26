plugins {
    id("vint.android.library")
}

android {
    namespace = "com.davidsimba.vintbeats.core.common"
}

dependencies {
    api(project(":core:model"))
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.androidx.activity.compose)
    implementation(libs.image.cropper)
}
