import java.util.zip.*

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
    id("com.github.rodm.teamcity-agent")
}

dependencies {
    compile(kotlin("stdlib"))
    compile(project(":common"))

    compile("com.google.code.gson:gson:2.2.4")
    compile("org.apache.httpcomponents:httpclient:4.2.6")

    /// BuildProcessFacade
    compileOnly("org.jetbrains.teamcity.internal:agent:${rootProject.ext["teamcityVersion"]}")
}

val fix_dependencies = configurations.create("fix_dependencies")

project.afterEvaluate {
    configurations.compile.dependencies.forEach { dep ->
        if (dep.group?.startsWith("org.jetbrains.teamcity.internal") != false) {
            return@forEach
        }

        if (dep !is ExternalModuleDependency) {
            return@forEach
        }

        fix_dependencies.dependencies.add(dep.copy())
    }
}

teamcity {
    version = rootProject.extra["teamcityVersion"] as String

    agent {
        descriptor {
            pluginDeployment {
                useSeparateClassloader = true
            }
        }

        files {
            into("lib") {
                from(fix_dependencies)
            }
        }
    }
}

tasks.withType<Jar> {
    baseName = "teamcity-node-agent"
}

tasks["agentPlugin"].doLast {
    val zipTask = tasks["agentPlugin"] as Zip
    val zipFile = zipTask.archivePath

    val entries = zipFile.inputStream().use { it ->
        ZipInputStream(it).use { z ->
            generateSequence { z.nextEntry }
                    .filterNot { it.isDirectory }
                    .map { it.name }
                    .toList()
                    .sorted()
        }
    }

    println("\n\nDetected files under Agent plugin:${entries.joinToString(separator = "\n  - ", prefix = "\n  - ")}\n\n")

    val expectedFiles = listOf(
            "lib/annotations-13.0.jar",
            "lib/commons-codec-1.6.jar",
            "lib/commons-logging-1.1.1.jar",
            "lib/gson-2.2.4.jar",
            "lib/httpclient-4.2.6.jar",
            "lib/httpcore-4.2.5.jar",
            "lib/kotlin-stdlib-1.2.41.jar",
            "lib/teamcity-node-agent-$version.jar",
            "lib/teamcity-node-common-$version.jar",
            "teamcity-plugin.xml"
    )

    println("\n\nExpected files:${expectedFiles.joinToString(separator = "\n  - ", prefix = "\n  - ")}\n\n")

    if (entries != expectedFiles) {
        throw Error("agent plugin files does not match")
    }
}
