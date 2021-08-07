package deps

object Dokka : Group("org.jetbrains.dokka", "0.10.1") {
    object Gradle {
        val Plugin = artifact("dokka-gradle-plugin")
    }
}

object Kotlin : Group("org.jetbrains.kotlin", "1.5.21") {
    val StdLib = artifact("kotlin-stdlib")

    object Gradle {
        val Plugin = artifact("kotlin-gradle-plugin")
    }

    object Test {
        val Common = artifact("kotlin-test-common")
        val Jvm = artifact("kotlin-test")
    }
}

object Spek : Group("org.spekframework.spek2", "2.0.9") {
    object Dsl {
        val Jvm = artifact("spek-dsl-jvm")
    }

    object Runner {
        val JUnit5 = artifact("spek-runner-junit5")
    }
}
