import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.gradle.api.tasks.testing.logging.TestLogEvent.*
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
  kotlin("jvm") version "1.5.10"
  application
  id("com.github.johnrengelman.shadow") version "7.0.0"
}

group = "com.learning"
version = "1.0.0-SNAPSHOT"

repositories {
  mavenCentral()
}

val vertxVersion = "4.1.3"
val junitJupiterVersion = "5.7.0"

val mainVerticleName = "com.learning.vertx_web_tutorial.MainVerticle"
val launcherClassName = "io.vertx.core.Launcher"

val watchForChange = "src/**/*"
val doOnChange = "${projectDir}/gradlew classes"

application {
  mainClass.set(launcherClassName)
}

dependencies {
  implementation(platform("io.vertx:vertx-stack-depchain:$vertxVersion"))
  implementation("io.vertx:vertx-web")
  implementation("io.vertx:vertx-lang-kotlin")
  implementation(kotlin("stdlib-jdk8"))
  // https://mvnrepository.com/artifact/com.fasterxml.jackson.core/jackson-databind
  implementation("com.fasterxml.jackson.core:jackson-databind:2.12.5")
  // https://mvnrepository.com/artifact/ch.qos.logback/logback-classic
  implementation("ch.qos.logback", "logback-classic", "1.2.3")
  // https://mvnrepository.com/artifact/io.vertx/vertx-web-client
  implementation("io.vertx:vertx-web-client:4.1.3")
  implementation("org.projectlombok", "lombok", "1.18.20")
  annotationProcessor("org.projectlombok", "lombok", "1.18.20")
  // https://mvnrepository.com/artifact/io.vertx/vertx-config
  implementation("io.vertx:vertx-config:4.1.4")
  // https://mvnrepository.com/artifact/io.vertx/vertx-config-yaml
  implementation("io.vertx:vertx-config-yaml:4.1.4")
  // https://mvnrepository.com/artifact/org.flywaydb/flyway-core
  implementation("org.flywaydb:flyway-core:7.15.0")
  // https://mvnrepository.com/artifact/org.postgresql/postgresql
  implementation("org.postgresql:postgresql:42.2.23")
  // https://mvnrepository.com/artifact/io.vertx/vertx-pg-client
  implementation("io.vertx:vertx-pg-client:4.1.4")
  // https://mvnrepository.com/artifact/io.netty/netty-resolver-dns-native-macos
  implementation("io.netty", "netty-resolver-dns-native-macos", "4.1.68.Final", classifier = "osx-x86_64")
  // https://mvnrepository.com/artifact/io.vertx/vertx-sql-client-templates
  implementation("io.vertx:vertx-sql-client-templates:4.1.4")
  implementation("io.vertx:vertx-mysql-client:4.1.4")
  // https://mvnrepository.com/artifact/mysql/mysql-connector-java
  implementation("mysql:mysql-connector-java:8.0.26")



  testImplementation("org.projectlombok", "lombok", "1.18.20")
  testAnnotationProcessor("org.projectlombok", "lombok", "1.18.20")
  testImplementation("io.vertx:vertx-junit5")
  testImplementation("org.junit.jupiter:junit-jupiter:$junitJupiterVersion")

}

val compileKotlin: KotlinCompile by tasks
compileKotlin.kotlinOptions.jvmTarget = "11"

tasks.withType<ShadowJar> {
  archiveClassifier.set("fat")
  manifest {
    attributes(mapOf("Main-Verticle" to mainVerticleName))
  }
  mergeServiceFiles()
}

tasks.withType<Test> {
  useJUnitPlatform()
  testLogging {
    events = setOf(PASSED, SKIPPED, FAILED)
  }
}

tasks.withType<JavaExec> {
  args = listOf(
    "run",
    mainVerticleName,
    "--redeploy=$watchForChange",
    "--launcher-class=$launcherClassName",
    "--on-redeploy=$doOnChange"
  )
}
