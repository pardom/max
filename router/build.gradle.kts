import org.jetbrains.kotlin.gradle.plugin.mpp.AbstractKotlinNativeTargetPreset

val GROUP: String by project
val VERSION_NAME: String by project

group = GROUP
version = VERSION_NAME

plugins {
    kotlin("multiplatform")
}

repositories {
    jcenter()
}

kotlin {
    jvm()

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(deps.Kotlin.StdLib)
                api(project(":uri"))
                api(project(":matcher"))
            }
        }
        val jvmTest by getting {
            dependencies {
                implementation(deps.Kotlin.Test.Jvm)
                implementation(deps.Spek.Dsl.Jvm)
                runtimeOnly(deps.Spek.Runner.JUnit5)
            }
        }
        val nativeMain by creating {
            dependsOn(commonMain)
        }
    }

    targets {
        presets
            .filterIsInstance<AbstractKotlinNativeTargetPreset<*>>()
            .forEach { preset ->
                targetFromPreset(preset, preset.name) {
                    compilations["main"].source(sourceSets["nativeMain"])
                }
            }
    }
}

tasks.withType(Test::class) {
    useJUnitPlatform {
        includeEngines("spek2")
    }
}

apply("$rootDir/gradle/gradle-mvn-mpp-push.gradle")
