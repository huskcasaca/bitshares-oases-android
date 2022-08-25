plugins {
    id("com.android.application")
    id("kotlin-android")
    id("kotlin-kapt")
    kotlin("plugin.serialization") version "1.7.10"
}

configurations.all {
    resolutionStrategy {
        force("org.jetbrains.kotlin:kotlin-reflect:1.7.10")
    }
}

android {
    compileSdk = 32
    namespace = "com.bitshares.oases"

    defaultConfig {
        minSdk = 23
        targetSdk = 32
        multiDexEnabled = true
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables.useSupportLibrary = true
//        signingConfig = signingConfigs.debug
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    packagingOptions {
        resources.excludes.addAll(
            listOf(
                "DebugProbesKt.bin",
                "/META-INF/proguard/androidx-annotations.pro",
                "/META-INF/*.version",
                "/META-INF/*.kotlin_module",
                "/META-INF/services/**",
                "/META-INF/native/**",
                "/META-INF/native-image/**",
                "/META-INF/INDEX.LIST",
//                    "**/kotlin/**",
//                    "**/javax/**",
                "**/bouncycastle/**",
//                    "**/*.kotlin_*",
//                    "com/**",
//                    "org/**",
//                    "**/*.java",
//                    "**/*.proto"
            )
        )
    }
    configurations.all {
        resolutionStrategy.eachDependency {
            if (requested.group == "org.jetbrains.kotlin" && requested.name.contains("reflect") ) {
                useVersion("1.7.10")
            }
        }
        exclude(group = "androidx.fragment", module = "fragment")
//            exclude(group = "androidx.fragment", module = "fragment-ktx")
        exclude(group = "androidx.activity", module = "activity")
//            exclude(group = "androidx.activity", module = "activity-ktx")
//            exclude(group = "androidx.activity", module = "activity-compose")
    }
    lintOptions {
//            isShowAll = true
//            isCheckAllWarnings = true
        isCheckReleaseBuilds = false
//            isWarningsAsErrors = true
//            textOutput = project.file("build/lint.txt")
//            htmlOutput = project.file("build/lint.html")
    }

    defaultConfig {
        applicationId = "com.bitshares.oases"
        versionCode = 102
        versionName = "1.0.2-alpha"
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            isShrinkResources = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
        getByName("debug") {
            isMinifyEnabled = false
            isShrinkResources = false
        }
    }
}
dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.7.10")

    implementation("androidx.core:core-ktx:1.7.0")
    implementation("androidx.appcompat:appcompat:1.4.1")
    // Kotlin
    implementation("androidx.savedstate:savedstate:1.1.0")
    implementation("androidx.savedstate:savedstate-ktx:1.1.0")

    val lifecycle_version = "2.4.0"
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:$lifecycle_version")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:$lifecycle_version")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:$lifecycle_version")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:$lifecycle_version")
    implementation("androidx.lifecycle:lifecycle-viewmodel-savedstate:$lifecycle_version")
    // alternately - if using Java8, use the following instead of lifecycle-compiler
//        implementation("androidx.lifecycle:lifecycle-common-java8:$lifecycle_version")

    // optional - helpers for implementing LifecycleOwner in a Service
    implementation("androidx.lifecycle:lifecycle-service:$lifecycle_version")
    implementation("androidx.lifecycle:lifecycle-process:$lifecycle_version")
    implementation("androidx.lifecycle:lifecycle-reactivestreams-ktx:$lifecycle_version")

    implementation("androidx.room:room-ktx:2.4.3")
    kapt("androidx.room:room-compiler:2.4.3")


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
    implementation(project(":include:bitshares-kit:core"))
    implementation(project(":include:modulon"))
    implementation(project(":kdenticon"))
    implementation(project(":swirl"))

}

dependencies {
    implementation(project(":include:deprecated-java-json"))
    implementation(project(":include:deprecated-bitshares-kit"))
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.6.1")
    testImplementation("com.github.bilthon:graphenej:0.4.6")
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.slf4j:slf4j-jdk14:1.7.36")
    testImplementation("org.bouncycastle:bcpkix-jdk15on:1.70")
    testImplementation("org.bitcoinj:bitcoinj-core:0.16.1")

    androidTestImplementation("androidx.test.ext:junit:1.1.3")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.4.0")
}
