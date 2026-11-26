plugins {
    id("vint.android.feature")
}

android {
    namespace = "com.davidsimba.vintbeats.feature.search"
}

dependencies {
    implementation(project(":core:model"))
    implementation(project(":core:common"))
    implementation(project(":core:designsystem"))
    implementation(project(":core:data"))
    implementation(project(":core:ui"))
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.coil.compose)
    implementation(libs.androidx.compose.material.icons)
    implementation(libs.datastore.preferences)
    implementation(libs.gson)
    implementation(libs.androidx.activity.compose)
}
