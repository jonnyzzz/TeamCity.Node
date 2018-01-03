plugins {
  id("com.github.rodm.teamcity-common") version "1.0" apply false
  id("com.github.rodm.teamcity-agent") version "1.0" apply false
  id("com.github.rodm.teamcity-server") version "1.0" apply false

  kotlin("jvm") version "1.2.10" apply false
}


subprojects {

  repositories {
    jcenter()
  }

  plugins {
    java
  }
}
