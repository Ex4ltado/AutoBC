import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.5.31"
    id("com.github.johnrengelman.shadow") version "6.1.0"
    application
}

group = "me.joao.gabriel"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.sikulix:sikulixapi:2.0.5")
    implementation("com.github.joonasvali.naturalmouse:naturalmouse:2.0.3")
    //implementation("io.github.marcoslimaqa:sikulifactory:1.1.1")
}


tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

application {
    mainClass.set("autobc/AutoBCKt")
    mainClassName = "autobc/AutoBCKt"

    tasks.withType<Jar> {
        manifest {
            attributes["Main-Class"] = mainClass
        }
    }

}

tasks {
    shadowJar {
        archiveClassifier.set("")
    }
}