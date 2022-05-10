plugins {
    id("com.android.library")
    id("kotlin-android")
}

android {
    buildToolsVersion = Constants.BUILD_TOOLS_VERSION
    compileSdkVersion = Constants.COMPILE_SDK_VERSION
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
                useVersion(Constants.KOTLIN_VERSION)
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
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:${Constants.KOTLIN_VERSION}")
    implementation("org.jetbrains.kotlin:kotlin-reflect:${Constants.KOTLIN_VERSION}")

    implementation("androidx.constraintlayout:constraintlayout:2.1.3")
    implementation("androidx.recyclerview:recyclerview:1.2.1")
    implementation("androidx.drawerlayout:drawerlayout:1.1.1")
    implementation("androidx.viewpager2:viewpager2:1.1.0-beta01")
    implementation("androidx.dynamicanimation:dynamicanimation:1.0.0")
    implementation("androidx.coordinatorlayout:coordinatorlayout:1.2.0")

    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.jakewharton.retrofit:retrofit2-kotlinx-serialization-converter:0.8.0")

    // test libs
    testImplementation("junit:junit:4.13")
    androidTestImplementation("androidx.test.ext:junit:1.1.3")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.4.0")
}
// androidx
dependencies {
    implementation("androidx.core:core-ktx:1.7.0")
    implementation("androidx.appcompat:appcompat:1.4.1")
    // Kotlin
    implementation("androidx.savedstate:savedstate:1.1.0")
    implementation("androidx.savedstate:savedstate-ktx:1.1.0")
}
// androidx lifecycle
dependencies {
    val lifecycleVersion = "2.4.0"
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:$lifecycleVersion")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:$lifecycleVersion")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:$lifecycleVersion")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:$lifecycleVersion")
    implementation("androidx.lifecycle:lifecycle-viewmodel-savedstate:$lifecycleVersion")

    // optional - helpers for implementing LifecycleOwner in a Service
    implementation("androidx.lifecycle:lifecycle-service:$lifecycleVersion")
    implementation("androidx.lifecycle:lifecycle-process:$lifecycleVersion")
    implementation("androidx.lifecycle:lifecycle-reactivestreams-ktx:$lifecycleVersion")
}
// androidx activity
dependencies {
//    implementation("androidx.activity:activity:1.4.0")
//    implementation("androidx.activity:activity-ktx:1.4.0")
//    implementation("androidx.activity:activity-compose:1.4.0")
    api(files("${rootProject.projectDir}/external/androidx/activity/activity-debug.aar"))
    api(files("${rootProject.projectDir}/external/androidx/activity/activity-ktx-debug.aar"))
    api(files("${rootProject.projectDir}/external/androidx/activity/activity-compose-debug.aar"))
}

// androidx fragment
dependencies {
//    implementation("androidx.fragment:fragment:1.4.1")
//    implementation("androidx.fragment:fragment-ktx:1.4.1")
    api(files("${rootProject.projectDir}/external/androidx/fragment/fragment-debug.aar"))
    api(files("${rootProject.projectDir}/external/androidx/fragment/fragment-ktx-debug.aar"))
}
