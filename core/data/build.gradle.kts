plugins {
    id("vint.android.library")
    id("vint.android.hilt")
}

android {
    namespace = "com.davidsimba.vintbeats.core.data"
}

dependencies {
    api(project(":core:model"))
    api(project(":core:database"))
    api(project(":core:network"))
    api(project(":core:common"))

    implementation(libs.okhttp)
    implementation(libs.datastore.preferences)
    implementation(libs.kotlinx.coroutines.android)
    implementation("com.github.TeamNewPipe:NewPipeExtractor:v0.26.5")
}
