import org.gradlex.javamodule.moduleinfo.ExtraJavaModuleInfoPluginExtension
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.8.20"
    id("org.gradlex.extra-java-module-info") version "1.3"
    application
}

group = "org.example"
version = "1.0-SNAPSHOT"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

application {
    mainModule.set("org.example")
    mainClass.set("org.example.MainKt")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("scripting-jvm-host"))
}

// JPMS hack, see https://github.com/gradle/gradle/issues/17271
val compileJava: JavaCompile by tasks
val compileKotlin: KotlinCompile by tasks
compileKotlin.destinationDirectory.set(compileJava.destinationDirectory)

tasks.withType<Test> {
    useJUnitPlatform()
}


// quick and dirty way to define a module
fun ExtraJavaModuleInfoPluginExtension.defineModule(
    identifier: String,
    moduleName: String
) {
    module(identifier, moduleName) {
        exportAllPackages()
        requireAllDefinedDependencies()
    }
}

/*
Dependency Graph:

+--- org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.8.20
|    +--- org.jetbrains.kotlin:kotlin-stdlib:1.8.20
|    |    +--- org.jetbrains.kotlin:kotlin-stdlib-common:1.8.20
|    |    \--- org.jetbrains:annotations:13.0
|    \--- org.jetbrains.kotlin:kotlin-stdlib-jdk7:1.8.20
|         \--- org.jetbrains.kotlin:kotlin-stdlib:1.8.20 (*)
\--- org.jetbrains.kotlin:kotlin-scripting-jvm-host:1.8.20
     +--- org.jetbrains.kotlin:kotlin-script-runtime:1.8.20
     +--- org.jetbrains.kotlin:kotlin-stdlib:1.8.20 (*)
     +--- org.jetbrains.kotlin:kotlin-scripting-common:1.8.20
     |    \--- org.jetbrains.kotlin:kotlin-stdlib:1.8.20 (*)
     +--- org.jetbrains.kotlin:kotlin-scripting-jvm:1.8.20
     |    +--- org.jetbrains.kotlin:kotlin-script-runtime:1.8.20
     |    +--- org.jetbrains.kotlin:kotlin-stdlib:1.8.20 (*)
     |    \--- org.jetbrains.kotlin:kotlin-scripting-common:1.8.20 (*)
     +--- org.jetbrains.kotlin:kotlin-compiler-embeddable:1.8.20
     |    +--- org.jetbrains.kotlin:kotlin-stdlib:1.8.20 (*)
     |    +--- org.jetbrains.kotlin:kotlin-script-runtime:1.8.20
     |    +--- org.jetbrains.kotlin:kotlin-reflect:1.6.10
     |    +--- org.jetbrains.kotlin:kotlin-daemon-embeddable:1.8.20
     |    +--- org.jetbrains.intellij.deps:trove4j:1.0.20200330
     |    \--- net.java.dev.jna:jna:5.6.0
     \--- org.jetbrains.kotlin:kotlin-scripting-compiler-embeddable:1.8.20
          +--- org.jetbrains.kotlin:kotlin-scripting-compiler-impl-embeddable:1.8.20
          |    +--- org.jetbrains.kotlin:kotlin-scripting-common:1.8.20 (*)
          |    +--- org.jetbrains.kotlin:kotlin-scripting-jvm:1.8.20 (*)
          |    \--- org.jetbrains.kotlin:kotlin-stdlib:1.8.20 (*)
          \--- org.jetbrains.kotlin:kotlin-stdlib:1.8.20 (*)
 */
extraJavaModuleInfo {
    knownModule("org.jetbrains.kotlin:kotlin-stdlib", "kotlin.stdlib")
    defineModule("org.jetbrains.kotlin:kotlin-stdlib-common", "kotlin.stdlib.common")
    defineModule("org.jetbrains:annotations", "org.jetbrains.annotations")

    defineModule("org.jetbrains.kotlin:kotlin-scripting-jvm-host", "kotlin.scripting.jvm.host")
    defineModule("org.jetbrains.kotlin:kotlin-script-runtime", "kotlin.script.runtime")
    defineModule("org.jetbrains.kotlin:kotlin-scripting-common", "kotlin.scripting.common")
    defineModule("org.jetbrains.kotlin:kotlin-scripting-jvm", "kotlin.scripting.jvm")
    defineModule("org.jetbrains.kotlin:kotlin-compiler-embeddable", "kotlin.compiler.embeddable")
    defineModule("org.jetbrains.intellij.deps:trove4j", "trove4j")
    defineModule("org.jetbrains.kotlin:kotlin-daemon-embeddable", "kotlin.daemon.embeddable")
    defineModule("org.jetbrains.kotlin:kotlin-scripting-compiler-embeddable", "kotlin.scripting.compiler.embeddable")
    defineModule(
        "org.jetbrains.kotlin:kotlin-scripting-compiler-impl-embeddable", "kotlin.scripting.compiler.impl.embeddable"
    )
}
