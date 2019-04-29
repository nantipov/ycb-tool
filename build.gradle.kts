plugins {
    java
    application
    id("com.github.johnrengelman.shadow") version "5.0.0"
    id("io.freefair.lombok") version "3.2.0"
}

repositories {
    jcenter()
}

dependencies {
    implementation("com.google.guava:guava:27.1-jre")
    implementation("org.fusesource.jansi:jansi:1.18")

    implementation("com.fasterxml.jackson.core:jackson-core:2.9.8")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.9.8")
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:2.9.2")

    testImplementation("junit:junit:4.12")
}

application {
    mainClassName = "org.nantipov.ycb.tool.App"
}
