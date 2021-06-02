buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath(deps.Kotlin.Gradle.Plugin)
        classpath(deps.Dokka.Gradle.Plugin)
    }
}

