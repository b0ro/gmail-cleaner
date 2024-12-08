plugins {
  id("java")
  id("com.github.node-gradle.node") version "7.1.0"
}

node {
  version = "18.19.1"
  npmVersion = "9.2.0"
  download = true
}

tasks.named<Jar>("jar") {
  dependsOn("npm_run_build")
  from("dist/gmail-cleaner-frontend") {
    into("static")
  }
}
