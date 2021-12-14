import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.5.31"
    id("com.github.johnrengelman.shadow") version "6.1.0"
    application
}

group = "me.ex4ltado"
version = "1.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.sikulix:sikulixapi:2.0.5")
    implementation("com.github.joonasvali.naturalmouse:naturalmouse:2.0.3")
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

        doLast {
            val name = "AutoBC $version"

            val dir = file("build/$name/")
            if (dir.exists()) {
                dir.delete()
            }
            dir.mkdirs()

            val jarName = "${name}.jar"

            val jar = file(File(dir, jarName))
            val allJar = file("build/libs/autobc-${version}.jar")
            com.google.common.io.Files.copy(allJar, jar)

            val config = file(File(dir, "config.properties"))
            config.writeBytes(file("config.properties").readBytes())

            val dirImages = file(File(dir, "images"))
            val images = file("images")

            dirImages.mkdirs()

            org.apache.commons.io.FileUtils.copyDirectory(images, dirImages)
        }
    }


}