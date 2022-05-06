plugins {
    id("com.android.application")
    id("kotlin-android")
    id("kotlin-kapt")
    kotlin("plugin.serialization") version Constants.KOTLIN_VERSION
}

application()

dependencies {
//    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:${Constants.KOTLIN_VERSION}")
    implementation("org.jetbrains.kotlin:kotlin-reflect:${Constants.KOTLIN_VERSION}")
    implementation("org.jetbrains.kotlinx:kotlinx-io-jvm:0.1.16")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.6.1")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.2")
    implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.3.2")


    // third party libs
    implementation("org.java-websocket:Java-WebSocket:1.4.1")
    implementation("com.google.android.material:material:1.7.0-alpha01")
    implementation("com.squareup.retrofit2:retrofit:2.9.0")

    // TODO: 2022/2/8 upgrade to okhttp4
    implementation("com.jakewharton.retrofit:retrofit2-kotlinx-serialization-converter:0.8.0")
    implementation("com.squareup.okhttp3:okhttp:4.9.3")

    // project libs
    implementation(project(":library:bitshares-kit"))
    implementation(project(":library:bitshares-kit-old"))
    implementation(project(":library:modulon"))
    implementation(project(":library:kdenticon"))
    implementation(project(":library:swirl"))

    implementation(project(":external:java-json"))

    implementation(fileTree("${project.rootDir}/buildSrc/build/"))
}

dependencies {
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.6.1")
    testImplementation("com.github.bilthon:graphenej:0.4.6")
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.slf4j:slf4j-jdk14:1.7.36")
    testImplementation("org.bouncycastle:bcpkix-jdk15on:1.70")
    testImplementation("org.bitcoinj:bitcoinj-core:0.16.1")

    androidTestImplementation("androidx.test.ext:junit:1.1.3")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.4.0")
}

repositories {
    mavenCentral()
    maven {
        url = uri("https://maven.pkg.jetbrains.space/public/p/ktor/eap")
        name = "ktor-eap"
    }
}
