import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    kotlin("jvm") version "1.7.21"
    id("com.github.johnrengelman.shadow") version "7.1.2"
    
}

group = "fr.ftnl"
var botVersion = "1.0.0_0"
var mainClassName : String = "${group}.MainKt"

repositories {
    mavenCentral()
    maven("https://jitpack.io/")
}

dependencies {
    implementation("com.google.code.gson:gson:2.10")
    
    // discord
    implementation("net.dv8tion:JDA:5.0.0-beta.1") { exclude("opus-java") }
    implementation("com.github.minndevelopment:jda-ktx:17eb77a")
    
    // TODO : Débatre de l'utilisation d'un webhook pour posté les messages
    implementation("club.minnced:discord-webhooks:0.8.2")
    
    // reflection
    implementation("org.reflections:reflections:0.10.2")
    
    // Logger
    implementation("org.slf4j", "slf4j-api", "1.7.2")
    implementation("ch.qos.logback", "logback-classic", "1.2.9")
    implementation("ch.qos.logback", "logback-core", "1.2.9")
    
    // Exposed
    implementation("org.jetbrains.exposed:exposed-jodatime:0.41.1")
    implementation("org.jetbrains.exposed:exposed-core:0.39.2")
    implementation("org.jetbrains.exposed:exposed-jdbc:0.39.2")
    implementation("org.jetbrains.exposed:exposed-dao:0.39.2")
    implementation("mysql:mysql-connector-java:8.0.30")
    
    // Cache
    implementation("io.github.reactivecircus.cache4k:cache4k:0.7.0")
    
    implementation(kotlin("scripting-jsr223"))
    
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}


tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "17"
    kotlinOptions.freeCompilerArgs = listOf(
        "-Xjvm-default=all",  // use default methods in interfaces
        "-Xlambdas=indy"      // use invoke-dynamic lambdas instead of synthetic classes
    )
}


tasks.withType<ShadowJar> {
    doLast {
        val oldVersion = botVersion
        val splited = oldVersion.split("_")
        val newVersion = splited[1].toInt() + 1
        val newVersionString = "${splited[0]}_$newVersion"
        botVersion = newVersionString
        val s = buildFile
            .readText()
            .replaceFirst(oldVersion, botVersion)
        buildFile.writeText(s)
    }
    
    archiveBaseName.set("FTNL-MOD")
    archiveClassifier.set("")
    archiveVersion.set(botVersion)
}

tasks.withType<org.gradle.jvm.tasks.Jar> {
    manifest {
        attributes["Implementation-Title"] = "Grupo"
        attributes["Implementation-Version"] = botVersion
        attributes["Main-Class"] = mainClassName
    }

}