buildscript {
    repositories {
        jcenter()
    }
    dependencies {
        classpath("com.github.jengelman.gradle.plugins:shadow:6.1.0")
    }
}
plugins {
    kotlin("jvm") version "1.4.0"
    kotlin("plugin.serialization") version "1.4.0"
    kotlin("kapt") version "1.4.0"
    id("com.github.johnrengelman.shadow") version "6.1.0"
    id("java")
}
apply(plugin = "com.github.johnrengelman.shadow")
apply(plugin = "java")

group = "ltd.zake"
version = "1.0.1-20Dec10b"


repositories {
    mavenLocal()
    jcenter()
    mavenCentral()
}

kotlin.sourceSets.all {
    languageSettings.useExperimentalAnnotation("kotlin.RequiresOptIn")
}
dependencies {
    compileOnly(kotlin("stdlib-jdk8"))

    val core = "1.3.0"
    val console = "1.0-RC-dev-28"
    val jdbc_sqlite = "3.32.3"
    val ktorm = "3.2.0"
    compileOnly("net.mamoe:mirai-console:$console")
    compileOnly("net.mamoe:mirai-core:$core")
    implementation("org.xerial", "sqlite-jdbc", jdbc_sqlite)
    //implementation("org.ktorm", "ktorm-core", ktorm)

    val pool = "2.9.0"
    val dbpc = "2.8.0"
    val logging = "1.2"

    //implementation("org.apache.commons", "commons-dbcp2", dbpc)
    //implementation("org.apache.commons", "commons-pool2", pool)
    //implementation("org.apache.commons", "commons-logging", logging)


    val autoService = "1.0-rc7"
    kapt("com.google.auto.service", "auto-service", autoService)
    compileOnly("com.google.auto.service", "auto-service-annotations", autoService)

    testImplementation("net.mamoe:mirai-console:$console")
    testImplementation("net.mamoe:mirai-core:$core")
    //testImplementation("net.mamoe:mirai-console-pure:$console")
    testImplementation(kotlin("stdlib-jdk8"))
}

kotlin.target.compilations.all {
    kotlinOptions.freeCompilerArgs += "-Xjvm-default=enable"
    kotlinOptions.jvmTarget = "1.8"
}
