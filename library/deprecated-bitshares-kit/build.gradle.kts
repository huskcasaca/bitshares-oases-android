plugins {
    id("com.android.library")
    id("kotlin-android")
    id("kotlin-kapt")
    kotlin("plugin.serialization") version "1.7.10"
}

android {
    compileSdkVersion = "android-31"
    defaultConfig {
        minSdk = 23
        targetSdk = 31
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
}
android {
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
        getByName("debug") {
            isMinifyEnabled = false
        }
    }
    lint {
        checkReleaseBuilds = false
    }
    namespace = "bitshareskit"
}
dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.7.10")
}

dependencies {
    implementation("androidx.core:core-ktx:1.7.0")
    implementation("androidx.appcompat:appcompat:1.4.1")
    // Kotlin
    implementation("androidx.savedstate:savedstate:1.1.0")
    implementation("androidx.savedstate:savedstate-ktx:1.1.0")
}
dependencies {
    implementation("androidx.room:room-ktx:2.4.3")
}
dependencies {
    val ktor_version = "2.0.0"
    implementation("io.ktor:ktor-client-core:$ktor_version")
//        implementation("io.ktor:ktor-serialization:$ktor_version")
    implementation("io.ktor:ktor-serialization-kotlinx-json:$ktor_version")

    implementation("io.ktor:ktor-client-content-negotiation:$ktor_version")
    implementation("io.ktor:ktor-client-serialization:$ktor_version")
//        implementation("io.ktor:ktor-client-android:$ktor_version")
    implementation("io.ktor:ktor-client-okhttp:$ktor_version")
    implementation("io.ktor:ktor-client-cio:$ktor_version")
}
dependencies {
    implementation("org.jetbrains.kotlin:kotlin-reflect:1.7.10")
    implementation("org.jetbrains.kotlinx:kotlinx-io-jvm:0.1.16")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.6.0")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.2")
    implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.3.1")
    implementation(project(":library:depricated-java-json"))

    implementation(project(":library:bitshares-kit"))

    implementation("org.bouncycastle:bcpkix-jdk15on:1.65")

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