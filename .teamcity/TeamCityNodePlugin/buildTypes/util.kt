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

package TeamCityNodePlugin.buildTypes

import jetbrains.buildServer.configs.kotlin.v2017_2.BuildType
import jetbrains.buildServer.configs.kotlin.v2017_2.buildSteps.gradle
import jetbrains.buildServer.configs.kotlin.v2017_2.triggers.vcs

open class NodeBuildType(
        teamcityVersion : String,
        init: NodeBuildType.() -> Unit) : BuildType() {
    init {
        buildNumberPattern = "2.0.%build.counter%"
        uuid = "$teamcityVersion-63558112-92ee-41d6-a7f7-827c633c5325"
        id = "TeamCityNodePlugin_TeamCityNodeVs_${teamcityVersion.replace(Regex("[^a-zA-Z0-9]+"), "_")}"
        name = "TeamCity.Node vs $teamcityVersion"

        vcs {
            root(TeamCityNodePlugin.vcsRoots.git___github_com_jonnyzzz_TeamCity_Node_git)
        }

        steps {
            gradle {
                tasks = "teamcity"
                buildFile = "build.gradle.kts"
                useGradleWrapper = true
                jdkHome = "%env.JDK_18_x64%"
            }
        }

        init()

        params {
            param("env.DEP_TEAMCITY_VERSION", teamcityVersion)
        }

        inheritBuildNumber()
    }
}
