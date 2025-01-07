import com.vanniktech.maven.publish.SonatypeHost

plugins {
    id("java-library")
    id("maven-publish")
    id("com.vanniktech.maven.publish") version "0.30.0"
}

mavenPublishing {
    coordinates("io.github.oops418", "notion-to-md", "1.0.0")
    pom {
        name.set("notion-to-md")
        description.set("A Java library for converting Notion content to Markdown")
        inceptionYear.set("2025")
        url.set("https://github.com/Oops418/notion-to-md")
        licenses {
            license {
                name.set("The MIT License")
                url.set("https://opensource.org/licenses/MIT")
            }
        }
        developers {
            developer {
                id.set("Oops418")
                name.set("Oops418")
                url.set("https://github.com/Oops418")
            }
        }
        scm {
            url.set("https://github.com/Oops418/notion-to-md")
            connection.set("scm:git:git://github.com/Oops418/notion-to-md.git")
            developerConnection.set("scm:git:ssh://git@github.com/Oops418/notion-to-md.git")
        }
    }

    publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL)
    signAllPublications()
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