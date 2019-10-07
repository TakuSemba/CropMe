object Dep {

    object Version {
        const val appcompat = "26.0.0"
    }

    /** plugin */
    val pluginBuildGradle = "com.android.tools.build:gradle:3.2.1"
    val pluginBintrayGradle = "com.jfrog.bintray.gradle:gradle-bintray-plugin:1.8.1"
    val pluginAndroidMaven = "com.github.dcendents:android-maven-gradle-plugin:2.1"

    /** appcompat */
    val appcompat = "com.android.support:appcompat-v7:${Version.appcompat}"
    val dynamicAnimation = "com.android.support:support-dynamic-animation:${Version.appcompat}"
    val recyclerview = "com.android.support:recyclerview-v7:${Version.appcompat}"
    val design = "com.android.support:design:${Version.appcompat}"
    val exifinterface = "com.android.support:exifinterface:${Version.appcompat}"
}
