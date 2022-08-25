plugins {
    id("com.android.library")
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
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.7.10")
}

dependencies {
    implementation("androidx.annotation:annotation:1.1.0")
    implementation("androidx.vectordrawable:vectordrawable-animated:1.1.0")
}
android {
    namespace = "com.mattprecious.swirl"
}

