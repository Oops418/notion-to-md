plugins {
    id("java-library")
    id("maven-publish")
}

group = "com.github.oops404error"
version = "1.0-SNAPSHOT"

java {
//    withJavadocJar()
    withSourcesJar()
}

publishing {
    publications {
        create<MavenPublication>("libraryProject") {
            groupId = "com.github.oops404error"
            artifactId = "notion-to-md"
            version = "1.0-SNAPSHOT"

            from(components["java"])
        }
    }
}

repositories {
    mavenCentral()
}

buildscript {
    val notionSdkVersion by extra("1.11.1")
    val lombokVersion by extra("1.18.30")
    val slf4jVersion by extra("2.0.16")
    val log4jVersion by extra("2.24.3")
}

dependencies {
    val notionSdkVersion : String by rootProject.extra
    val lombokVersion : String by rootProject.extra
    val slf4jVersion : String by rootProject.extra
    val log4jVersion : String by rootProject.extra

    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")

    annotationProcessor("org.projectlombok:lombok:${lombokVersion}")
    compileOnly("org.projectlombok:lombok:${lombokVersion}")

    implementation("com.github.seratch:notion-sdk-jvm-core:${notionSdkVersion}")

    implementation("org.slf4j:slf4j-api:${slf4jVersion}")
    testImplementation("org.apache.logging.log4j:log4j-core:${log4jVersion}")
    testImplementation("org.apache.logging.log4j:log4j-api:${log4jVersion}")
    testImplementation("org.apache.logging.log4j:log4j-slf4j2-impl:${log4jVersion}")
}

tasks.test {
    useJUnitPlatform()
}