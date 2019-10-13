object Dep {

    object Version {
        const val kotlin = "1.3.50"
        const val androidx = "1.0.0"
        const val material = "1.0.0"
    }

    /** plugin */
    val pluginBuildGradle = "com.android.tools.build:gradle:3.5.1"
    val pluginBintrayGradle = "com.jfrog.bintray.gradle:gradle-bintray-plugin:1.8.4"
    val kotlinGradlePlugin = "org.jetbrains.kotlin:kotlin-gradle-plugin:${Version.kotlin}"

    /** kotlin */
    val kotlin = "org.jetbrains.kotlin:kotlin-stdlib:${Version.kotlin}"

    /** androidx */
    val appcompat = "androidx.appcompat:appcompat:${Version.androidx}"
    val dynamicAnimation = "androidx.dynamicanimation:dynamicanimation:${Version.androidx}"
    val recyclerview = "androidx.recyclerview:recyclerview:${Version.androidx}"
    val exifinterface = "androidx.exifinterface:exifinterface:${Version.androidx}"

    /** material */
    val material = "com.google.android.material:material:${Version.material}"
}
