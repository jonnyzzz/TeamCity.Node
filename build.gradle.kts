plugins {
  id("com.github.rodm.teamcity-common") version "1.1-beta-2" apply false
  id("com.github.rodm.teamcity-agent") version "1.1-beta-2" apply false
  id("com.github.rodm.teamcity-server") version "1.1-beta-2" apply false

  kotlin("jvm") version "1.2.10" apply false
}

ext {
  set("teamcityVersion", System.getenv("DEP_TEAMCITY_VERSION") ?: "10.0")
}

group = "org.jonnyzzz.teamcity.node"
version = System.getenv("BUILD_NUMBER") ?: "2.0-SNAPSHOT"


subprojects {

  repositories {
    jcenter()
  }

  plugins {
    java
  }

  group = rootProject.group
  version = rootProject.version
}

task("teamcity") {
  dependsOn(":tests:test")
  dependsOn(":server:teamcity")
}
