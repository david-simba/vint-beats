plugins {
    id("vint.android.library")
}

android {
    namespace = "com.davidsimba.vintbeats.core.common"
}

dependencies {
    implementation(libs.androidx.activity.compose)
    implementation(libs.image.cropper)
}
