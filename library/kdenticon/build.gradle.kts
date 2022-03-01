plugins {
    id("com.android.library")
    id("kotlin-android")
}

library()

dependencies {
//    implementation fileTree(dir: "libs", include: ["*.jar"])
//    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:${Constants.KOTLIN_VERSION}")

//    implementation("androidx.appcompat:appcompat:1.2.0")
//    implementation("androidx.core:core-ktx:1.6.0-alpha02")
    api("com.caverock:androidsvg-aar:1.4")
    testImplementation("junit:junit:4.13")
    androidTestImplementation("androidx.test.ext:junit:1.1.3")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.4.0")
//    implementation("androidx.core:core-ktx:+")

}
