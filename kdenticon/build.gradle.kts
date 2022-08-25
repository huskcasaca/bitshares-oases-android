plugins {
    id("com.android.library")
    id("kotlin-android")
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
    namespace = "kdenticon"
}

dependencies {
//    implementation fileTree(dir: "libs", include: ["*.jar"])

    api("com.caverock:androidsvg-aar:1.4")
    testImplementation("junit:junit:4.13")
    androidTestImplementation("androidx.test.ext:junit:1.1.3")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.4.0")

}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.7.10")
}