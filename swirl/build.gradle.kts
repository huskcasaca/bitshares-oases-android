plugins {
    id("com.android.library")
}

android {
    compileSdk = 32
    namespace = "com.mattprecious.swirl"
    defaultConfig {
        minSdk = 23
        targetSdk = 32
    }
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.7.10")
    implementation("androidx.annotation:annotation:1.4.0")
    implementation("androidx.vectordrawable:vectordrawable-animated:1.1.0")
}
