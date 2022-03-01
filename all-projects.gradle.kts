rootProject.extra.apply {
    set("pluginVersion", "7.0.4")
    set("kotlinVersion", "1.6.10")
//    val androidPluginVersion = rootProject.extra["androidPluginVersion"].toString()
//    val kotlinVersion = rootProject.extra["kotlinVersion"].toString()
}

repositories {
    google()
    mavenCentral()
}

//tasks.withType<KotlinCompile>().all {
//    kotlinOptions {
//        freeCompilerArgs = freeCompilerArgs + "-Xuse-experimental=kotlin.ExperimentalUnsignedTypes" + "-Xuse-experimental=kotlin.contracts.ExperimentalContracts" + "-Xopt-in=kotlin.ExperimentalStdlibApi"
//    }
//}