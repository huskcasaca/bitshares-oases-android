plugins {
    id("com.android.library")
    id("kotlin-android")
    id("kotlin-kapt")
    kotlin("plugin.serialization") version "1.7.10"
}

android {
    compileSdk = 32
    namespace = "bitshareskit"
    defaultConfig {
        minSdk = 23
        targetSdk = 32
        multiDexEnabled = true
    }
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.7.10")
    implementation("androidx.core:core-ktx:1.7.0")
    implementation("androidx.appcompat:appcompat:1.4.1")
    // Kotlin
    implementation("androidx.savedstate:savedstate:1.1.0")
    implementation("androidx.savedstate:savedstate-ktx:1.1.0")
    implementation("androidx.room:room-ktx:2.4.3")
    val ktor_version = "2.0.0"
    implementation("io.ktor:ktor-client-core:$ktor_version")
//        implementation("io.ktor:ktor-serialization:$ktor_version")
    implementation("io.ktor:ktor-serialization-kotlinx-json:$ktor_version")

    implementation("io.ktor:ktor-client-content-negotiation:$ktor_version")
    implementation("io.ktor:ktor-client-serialization:$ktor_version")
//        implementation("io.ktor:ktor-client-android:$ktor_version")
    implementation("io.ktor:ktor-client-okhttp:$ktor_version")
    implementation("io.ktor:ktor-client-cio:$ktor_version")

    implementation("org.jetbrains.kotlin:kotlin-reflect:1.7.10")
    implementation("org.jetbrains.kotlinx:kotlinx-io-jvm:0.1.16")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.6.0")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.2")
    implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.3.1")
    implementation(project(":include:deprecated-java-json"))

    implementation(project(":include:bitshares-kit:core"))

    implementation("org.bouncycastle:bcpkix-jdk15on:1.65")
}
dependencies {

    // test libs
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.6.0")
    testImplementation("com.github.bilthon:graphenej:0.4.2")
    testImplementation("com.github.bilthon:graphenej:0.4.2")
    testImplementation("junit:junit:4.13")
    testImplementation("org.slf4j:slf4j-jdk14:1.7.30")
    testImplementation("org.bouncycastle:bcpkix-jdk15on:1.65")
    testImplementation("org.bitcoinj:bitcoinj-core:0.14.3")

    androidTestImplementation("androidx.test.ext:junit:1.1.3")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.4.0")
}