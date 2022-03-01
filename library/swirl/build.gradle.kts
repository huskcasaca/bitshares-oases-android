plugins {
    id("com.android.library")
}

library()

//android {
//    compileSdkVersion = Constants.COMPILE_SDK_VERSION
//    buildToolsVersion = Constants.BUILD_TOOLS_VERSION
//
//    defaultConfig {
//        minSdk = 26
//        targetSdk = 31
//        vectorDrawables.useSupportLibrary = true
//    }
//    resourcePrefix("swirl_")
//}

dependencies {
    implementation("androidx.annotation:annotation:1.1.0")
    implementation("androidx.vectordrawable:vectordrawable-animated:1.1.0")
}

