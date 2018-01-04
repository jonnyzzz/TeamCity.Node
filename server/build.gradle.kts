import com.github.rodm.teamcity.TeamCityEnvironment
import com.github.rodm.teamcity.TeamCityServerPlugin
import org.jetbrains.kotlin.utils.addToStdlib.cast

/*
 * Copyright 2013-2018 Eugene Petrenko
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


plugins {
    kotlin("jvm")
    id("com.github.rodm.teamcity-server")
}

dependencies {
    compile(kotlin("stdlib"))
    compile(project(":common"))

    ///for BuildProblemManager
    compileOnly("org.jetbrains.teamcity.internal:server:${rootProject.ext["teamcityVersion"]}")

    agent(project(path = ":agent", configuration = "plugin"))
}

teamcity {
    // Use TeamCity 8.1 API
    version = rootProject.ext["teamcityVersion"] as String

    server {
        descriptor {
            name = "jonnyzzz.node"
            displayName = "Node.js build runner"
            version = rootProject.version as String?
            vendorName = "Eugene Petrenko"
            vendorUrl = "http://jonnyzzz.com"
            description = "A set of runners for Node.js stack"
            email = "eugene.petrenko@gmail.com"
            useSeparateClassloader = true
        }
    }

    environments {
        operator fun String.invoke(block: TeamCityEnvironment.() -> Unit) {
            environments.create(this, closureOf(block))
        }

        "teamcity2017.1" {
            version = "2017.1"
        }

        "teamcity2017.2" {
            version = "2017.2"
        }
    }
}


tasks.withType<Jar> {
    baseName = "teamcity-node-server"
}


task("teamcity") {
    dependsOn("serverPlugin")

    doLast {
        println("##teamcity[publishArtifacts '${(tasks["serverPlugin"] as Zip).archivePath}']")
    }
}
