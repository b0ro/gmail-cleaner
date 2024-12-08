import com.github.gradle.node.npm.task.NpmTask


plugins {
  id("com.github.node-gradle.node") version "7.1.0"
}

version = "0.0.1"

node {
  version = "18.19.1"
  npmVersion = "9.2.0"
  download = true
  workDir = file("${layout.buildDirectory.get()}/node")
}

tasks.register<NpmTask>("build") {
  args = listOf("run", "build")
}

tasks.named("build") {
  dependsOn("npm_install")
}
