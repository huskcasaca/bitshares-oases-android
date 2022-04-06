import org.gradle.api.JavaVersion
import org.gradle.api.Project
import org.gradle.kotlin.dsl.*
import com.android.build.gradle.TestedExtension
import org.gradle.api.artifacts.Dependency
import org.gradle.api.artifacts.dsl.DependencyHandler

private fun Project.android(block :TestedExtension.() -> Unit) = extensions.getByName<TestedExtension>("android").apply(block)

private fun DependencyHandler.api(dependencyNotation: Any): Dependency? = add("api", dependencyNotation)
private fun DependencyHandler.implementation(dependencyNotation: Any): Dependency? = add("implementation", dependencyNotation)
private fun DependencyHandler.kapt(dependencyNotation: Any): Dependency? = add("kapt", dependencyNotation)
private fun DependencyHandler.testImplementation(dependencyNotation: Any): Dependency? = add("testImplementation", dependencyNotation)
private fun DependencyHandler.androidTestImplementation(dependencyNotation: Any): Dependency? = add("androidTestImplementation", dependencyNotation)

fun Project.application() {
    configurations.all {
        resolutionStrategy {
            force("org.jetbrains.kotlin:kotlin-reflect:${Constants.KOTLIN_VERSION}")
        }
    }
    basics()
    android {
        defaultConfig {
            applicationId = Constants.PROJECT_PACKAGE_NAME
            versionCode = Constants.PROJECT_VERSION_CODE
            versionName = Constants.PROJECT_VERSION
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
    dependenciesKotlinStdlib()
    dependenciesAndroidx()
    dependenciesAndroidxLifecycle()
    dependenciesAndroidxRoomKapt()

    dependenciesKtor()
}

fun Project.library() {
    basics()
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
    dependenciesKotlinStdlib()
}

fun Project.libraryModulon() {
    dependenciesAndroidx()
    dependenciesAndroidxLifecycle()
    dependenciesAndroidxActivity()
    dependenciesAndroidxFragment()
    dependencies {
        implementation("org.jetbrains.kotlin:kotlin-reflect:${Constants.KOTLIN_VERSION}")
        implementation("androidx.constraintlayout:constraintlayout:2.0.1")
        implementation("androidx.recyclerview:recyclerview:1.2.0-beta01")
        implementation("androidx.drawerlayout:drawerlayout:1.1.0")
        implementation("androidx.viewpager2:viewpager2:1.1.0-alpha01")
        implementation("androidx.dynamicanimation:dynamicanimation:1.0.0")
        implementation("androidx.coordinatorlayout:coordinatorlayout:1.1.0")

        implementation("com.squareup.retrofit2:retrofit:2.9.0")
        implementation("com.jakewharton.retrofit:retrofit2-kotlinx-serialization-converter:0.8.0")

        // test libs
        testImplementation("junit:junit:4.13")
        androidTestImplementation("androidx.test.ext:junit:1.1.3")
        androidTestImplementation("androidx.test.espresso:espresso-core:3.4.0")
    }
}
fun Project.libraryKit() {
    dependenciesAndroidx()
    dependenciesAndroidxRoom()
    dependenciesKtor()
    dependencies {
        implementation("org.jetbrains.kotlin:kotlin-reflect:${Constants.KOTLIN_VERSION}")
        implementation("org.jetbrains.kotlinx:kotlinx-io-jvm:0.1.16")
        implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.6.0")
        implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.2")
        implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.3.1")

        implementation(project(":external:java-json"))
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
        testImplementation(project(":external:java-json"))
        testImplementation(project(":library:bitshares-kit"))

        androidTestImplementation("androidx.test.ext:junit:1.1.3")
        androidTestImplementation("androidx.test.espresso:espresso-core:3.4.0")
    }
}

private fun Project.dependenciesKotlinStdlib() {
    dependencies {
        implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:${Constants.KOTLIN_VERSION}")
    }
}
private fun Project.dependenciesAndroidx() {
    dependencies {
        implementation("androidx.core:core-ktx:1.7.0")
        implementation("androidx.appcompat:appcompat:1.2.0")
        // Kotlin
        implementation("androidx.savedstate:savedstate:1.1.0")
        implementation("androidx.savedstate:savedstate-ktx:1.1.0")
    }
}
private fun Project.dependenciesAndroidxRoomKapt() {
    dependencies {
        implementation("androidx.room:room-ktx:2.4.1")
        kapt("androidx.room:room-compiler:2.4.1")
    }
}
private fun Project.dependenciesAndroidxRoom() {
    dependencies {
        implementation("androidx.room:room-ktx:2.4.1")
    }
}
private fun Project.dependenciesAndroidxLifecycle() {
    dependencies {
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
    }
}
private fun Project.dependenciesAndroidxActivity() {
    dependencies {
//         implementation("androidx.activity:activity:1.4.0")
        api(files("${rootProject.projectDir}/external/androidx/activity/activity-debug.aar"))
//         implementation("androidx.activity:activity-ktx:1.4.0")
        api(files("${rootProject.projectDir}/external/androidx/activity/activity-ktx-debug.aar"))
        // implementation("androidx.activity:activity-ktx:1.4.0")
        api(files("${rootProject.projectDir}/external/androidx/activity/activity-compose-debug.aar"))
    }

}
private fun Project.dependenciesAndroidxFragment() {
    dependencies {
        // implementation("androidx.fragment:fragment:1.4.1")
        api(files("${rootProject.projectDir}/external/androidx/fragment/fragment-debug.aar"))
        // implementation("androidx.fragment:fragment-ktx:1.4.1")
        api(files("${rootProject.projectDir}/external/androidx/fragment/fragment-ktx-debug.aar"))
    }
}
private fun Project.dependenciesKtor() {
    dependencies {
        val ktor_version = "2.0.0-beta-1"
        implementation("io.ktor:ktor-client-core:$ktor_version")
//        implementation("io.ktor:ktor-serialization:$ktor_version")
        implementation("io.ktor:ktor-serialization-kotlinx-json:$ktor_version")

        implementation("io.ktor:ktor-client-content-negotiation:$ktor_version")
        implementation("io.ktor:ktor-client-serialization:$ktor_version")
//        implementation("io.ktor:ktor-client-android:$ktor_version")
        implementation("io.ktor:ktor-client-okhttp:$ktor_version")
        implementation("io.ktor:ktor-client-cio:$ktor_version")
    }
}

private fun Project.basics() {
    android {
        buildToolsVersion = Constants.BUILD_TOOLS_VERSION
        compileSdkVersion = Constants.COMPILE_SDK_VERSION
        defaultConfig {
            minSdk = 26
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
}