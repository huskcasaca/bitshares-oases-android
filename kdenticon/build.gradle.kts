plugins {
    id("com.android.library")
    id("kotlin-android")
}

//apply("${rootProject.projectDir}/common.gradle.kts")

android {
    namespace = "kdenticon"
    compileSdk = 32
    defaultConfig {
        minSdk = 23
        targetSdk = 32
    }
}

dependencies {
    api("com.caverock:androidsvg-aar:1.4")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.3")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.4.0")

    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.7.10")

}