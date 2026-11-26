plugins {
    id("vint.android.library")
    id("vint.android.hilt")
}

android {
    namespace = "com.davidsimba.vintbeats.core.database"
}

dependencies {
    api(project(":core:model"))
    api(libs.room.runtime)
    api(libs.room.ktx)
    implementation(libs.kotlinx.coroutines.android)
    ksp(libs.room.compiler)
}
