plugins {
    kotlin("jvm") version "1.6.20"
    `java-gradle-plugin`
    `kotlin-dsl`
}

apply("../all-projects.gradle.kts")

dependencies {
    val pluginVersion = rootProject.extra["pluginVersion"].toString()
    val kotlinVersion = rootProject.extra["kotlinVersion"].toString()
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:$kotlinVersion")
    implementation("com.android.tools.build:gradle:$pluginVersion")
    implementation("com.android.tools.build:gradle-api:$pluginVersion")
    implementation(kotlin("gradle-plugin", kotlinVersion))
    implementation(kotlin("stdlib", kotlinVersion))

}

