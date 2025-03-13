import com.microsoft.azure.gradle.configuration.GradleRuntimeConfig
import io.gitlab.arturbosch.detekt.Detekt
import org.jlleitschuh.gradle.ktlint.tasks.KtLintCheckTask


group = "com.github.jensbarthel.azureplayground.kotlin-spring-cloud-function"
version = "1.0-SNAPSHOT"

buildscript {
    repositories { mavenCentral() }
    dependencies {
        classpath("org.springframework.boot:spring-boot-gradle-plugin:[3.0,4.0)")
        classpath("org.jetbrains.kotlin:kotlin-noarg:[1.4.0,2.2)")
        classpath("org.jetbrains.kotlin:kotlin-allopen:[1.4.0,2.2)")

        // See https://github.com/microsoft/azure-gradle-plugins/issues/192#issuecomment-2720392919
        // Remove when fixed in >1.16.1
        classpath("com.microsoft.azure:azure-functions-gradle-plugin:1.16.1") {
            exclude(group = "com.microsoft.azure", module = "azure-toolkit-appservice-lib")
        }
        classpath("com.microsoft.azure:azure-toolkit-appservice-lib:0.52.0")
    }
}

plugins {
    kotlin("jvm") version "[1.4.0, 2.0)"
    id("org.jlleitschuh.gradle.ktlint") version "[10.0, 12.0)"
    id("io.gitlab.arturbosch.detekt") version "[1.20, 2.0)"
    kotlin("plugin.spring") version "[1.4.0,2.2)"
    id("org.jetbrains.kotlin.plugin.serialization") version "[1.4.0,2.2)"
    id("com.microsoft.azure.azurefunctions") version "1.16.1"
    id("org.jetbrains.kotlin.plugin.jpa") version "[1.4.0,2.2)"
}

val springCloudVersion = "[4.0,5.0)"
val springBootVersion = "[3.0,4.0)"

repositories { mavenCentral() }
dependencies {
    implementation(kotlin("stdlib"))
    implementation(kotlin("reflect"))
    implementation(platform("org.springframework.cloud:spring-cloud-function-dependencies:$springCloudVersion"))
    implementation(platform("org.springframework.boot:spring-boot-dependencies:$springBootVersion"))
    implementation("com.microsoft.azure.functions:azure-functions-java-library")
    implementation("org.springframework.cloud:spring-cloud-function-adapter-azure")
    implementation("org.springframework.cloud:spring-cloud-function-kotlin")
    implementation("org.springframework.cloud:spring-cloud-function-context")

    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:[1.4.0,2.2)")
}

azurefunctions {
    val stage = Stage.DEV

    region = "swedencentral"
    resourceGroup = stage.resourceGroup
    appName = stage.appname
    deploymentStorageResourceGroup = "azureplayground"

    instanceMemory = 2048
    appServicePlanName = stage.appname
    pricingTier = "flex consumption"
    setAlwaysReadyInstances(
        closureOf<MutableMap<String, Int>> {
            put("http", 1)
        }
    )

    localDebug = "transport=dt_socket,server=y,suspend=n,address=5005"

    setRuntime(
        closureOf<GradleRuntimeConfig> {
            os("Linux")
            javaVersion("17")
        }
    )
    setAppSettings(
        closureOf<MutableMap<String, String>> {
            put("SPRING_PROFILES_ACTIVE", stage.internalProfile)
            put("ApplicationInsightsAgent_EXTENSION_VERSION", "~4")
            put("MAIN_CLASS", "playground.app.PlaygroundApplication")
        }
    )
}

tasks.withType<Jar> {
    manifest {
        attributes["Main-Class"] = "playground.app.PlaygroundApplication"
    }
}

enum class Stage(val resourceGroup: String, val internalProfile: String) {
    DEV("azureplayground", "dev");

    val appname get() = "ndispringflexapp$internalProfile"
    val planName get() = "asp-ndispringflexapp$internalProfile"
}

val compileKotlin: org.jetbrains.kotlin.gradle.tasks.KotlinCompile by tasks
compileKotlin.kotlinOptions {
    jvmTarget = "17"
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

tasks.withType<KtLintCheckTask> {
    dependsOn("ktlintFormat")
}

tasks.withType<Detekt> {
    dependsOn("runKtlintFormatOverMainSourceSet")
}
