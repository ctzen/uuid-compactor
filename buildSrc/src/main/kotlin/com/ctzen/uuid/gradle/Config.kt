package com.ctzen.uuid.gradle

object Config {

    /**
     * Versions
     */
    object Vers {
        const val gradle = "5.1"
    }

    /**
     * Dependencies
     */
    object Deps {
        const val libCommonsCodec = "commons-codec:commons-codec:1.11"

        // test dependencies
        const val libAssertj = "org.assertj:assertj-core:3.11.1"
        const val libTestNg = "org.testng:testng:6.14.3"
        const val libReportNg = "org.uncommons:reportng:1.1.4"
        const val libGuice = "com.google.inject:guice:4.2.2"   // reportng dependencies
    }

    val javaCompilerArgs = arrayOf(
            "-Xlint:unchecked",
            "-Xlint:deprecation"
    )

}
