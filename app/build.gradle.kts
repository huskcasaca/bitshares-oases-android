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
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.6.0")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.2")
    implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.3.1")
    
    implementation("androidx.constraintlayout:constraintlayout:2.1.3")
    implementation("androidx.recyclerview:recyclerview:1.2.1")
    implementation("androidx.drawerlayout:drawerlayout:1.1.1")
    implementation("androidx.viewpager2:viewpager2:1.1.0-beta01")
    implementation("androidx.dynamicanimation:dynamicanimation:1.0.0")
    implementation("androidx.coordinatorlayout:coordinatorlayout:1.2.0")

    // third party libs
    implementation("org.java-websocket:Java-WebSocket:1.4.1")
    implementation("com.google.android.material:material:1.3.0-alpha02")
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

    // test libs
    testImplementation("junit:junit:4.13")
    testImplementation("org.slf4j:slf4j-jdk14:1.7.35")

    androidTestImplementation("androidx.test.ext:junit:1.1.3")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.4.0")
}

repositories {
    mavenCentral()
}
