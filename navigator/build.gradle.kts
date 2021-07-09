import org.jetbrains.kotlin.gradle.plugin.mpp.AbstractKotlinNativeTargetPreset

plugins {
    kotlin("multiplatform")
    id("com.vanniktech.maven.publish")
}

kotlin {
    jvm()

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

//signing {
//    val SIGNING_PRIVATE_KEY: String? by project
//    val SIGNING_PASSWORD: String? by project
//    useInMemoryPgpKeys(SIGNING_PRIVATE_KEY, SIGNING_PASSWORD)
//}

tasks.withType(Sign::class) {
    enabled = false
}

tasks.withType(Test::class) {
    useJUnitPlatform {
        includeEngines("spek2")
    }
}
