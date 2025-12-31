plugins {
    java
    id("org.springframework.boot") version "4.0.1"
}

group = "nu.larsson.stefan"
version = "1.0-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(25)
    }
}

repositories {
    mavenCentral()
    maven { url = uri("https://repo.spring.io/milestone") }
}

dependencies {
    implementation(platform("org.springframework.boot:spring-boot-dependencies:4.0.1"))
    implementation(platform("org.springframework.ai:spring-ai-bom:2.0.0-M1"))

    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.springframework:spring-web") // Required by Spring AI MCP autoconfiguration
    implementation("org.springframework.ai:spring-ai-starter-mcp-server")
    implementation("org.springframework.ai:spring-ai-mcp-annotations")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.test {
    useJUnitPlatform()
}
