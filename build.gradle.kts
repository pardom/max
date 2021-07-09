buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath(deps.Kotlin.Gradle.Plugin)
        classpath(deps.Dokka.Gradle.Plugin)
        classpath("com.vanniktech:gradle-maven-publish-plugin:0.17.0")
    }
}

