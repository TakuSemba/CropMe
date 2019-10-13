object Dep {

    object Version {
        const val appcompat = "26.0.0"
        const val kotlin = "1.3.50"
    }

    /** plugin */
    val pluginBuildGradle = "com.android.tools.build:gradle:3.5.1"
    val pluginBintrayGradle = "com.jfrog.bintray.gradle:gradle-bintray-plugin:1.8.4"
    val kotlinGradlePlugin = "org.jetbrains.kotlin:kotlin-gradle-plugin:${Version.kotlin}"

    /** kotlin */
    val kotlin = "org.jetbrains.kotlin:kotlin-stdlib:${Version.kotlin}"

    /** appcompat */
    val appcompat = "com.android.support:appcompat-v7:${Version.appcompat}"
    val dynamicAnimation = "com.android.support:support-dynamic-animation:${Version.appcompat}"
    val recyclerview = "com.android.support:recyclerview-v7:${Version.appcompat}"
    val design = "com.android.support:design:${Version.appcompat}"
    val exifinterface = "com.android.support:exifinterface:${Version.appcompat}"
}
