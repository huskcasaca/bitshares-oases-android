import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.6.10")
    }
    //    dependencies {
//        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:${Constants.KOTLIN_VERSION}")
//        classpath("com.android.tools.build:gradle:7.0.4")
//        classpath(kotlin("gradle-plugin", Constants.KOTLIN_VERSION))
//    }
}

allprojects {
    apply("${rootProject.projectDir}/all-projects.gradle.kts")
}
