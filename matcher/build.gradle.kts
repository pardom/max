import org.jetbrains.kotlin.gradle.plugin.mpp.AbstractKotlinNativeTargetPreset

plugins {
    kotlin("multiplatform")
    id("com.vanniktech.maven.publish")
}

kotlin {
    jvm()

    js {
        browser()
        nodejs()
    }

    val onPhone = System.getenv("SDK_NAME")?.startsWith("iphoneos") ?: false
    if (onPhone) {
        iosArm64("ios")
    } else {
        iosX64("ios")
    }

    tvos()
    watchos()

    linuxX64()
    macosX64()
    mingwX64()

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation(deps.Kotlin.StdLib)
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
