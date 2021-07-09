import org.jetbrains.kotlin.gradle.plugin.mpp.AbstractKotlinNativeTargetPreset

plugins {
    kotlin("multiplatform")
    id("com.vanniktech.maven.publish")
}

kotlin {
    jvm()

    val sdkName: String? = System.getenv("SDK_NAME")
    val isiOSDevice = sdkName.orEmpty().startsWith("iphoneos")
    if (isiOSDevice) {
        iosArm64("iOS")
    } else {
        iosX64("iOS")
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(deps.Kotlin.StdLib)
                api(project(":matcher"))
                api(project(":router"))
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

tasks.withType(Sign::class) {
    enabled = false
}

tasks.withType(Test::class) {
    useJUnitPlatform {
        includeEngines("spek2")
    }
}
