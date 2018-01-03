package TeamCityNodePlugin.buildTypes

import jetbrains.buildServer.configs.kotlin.v2017_2.*
import jetbrains.buildServer.configs.kotlin.v2017_2.buildSteps.gradle
import jetbrains.buildServer.configs.kotlin.v2017_2.ideaRunner
import jetbrains.buildServer.configs.kotlin.v2017_2.triggers.vcs

object TeamCityNodePlugin_TeamCityNodeGradle : BuildType({
    uuid = "576e3f87-4778-4e9f-8b27-f351ac47dc1d"
    id = "TeamCityNodePlugin_TeamCityNodeGradle"
    name = "TeamCity.Node Gradle"

    buildNumberPattern = "2.0.%build.counter%"

    vcs {
        root(TeamCityNodePlugin.vcsRoots.git___github_com_jonnyzzz_TeamCity_Node_git)

        checkoutMode = CheckoutMode.ON_SERVER
    }

    steps {
        gradle {
            tasks = "teamcity"
            buildFile = "build.gradle.kts"
            useGradleWrapper = true
            jdkHome = "%env.JDK_18_x64%"
            param("org.jfrog.artifactory.selectedDeployableServer.defaultModuleVersionConfiguration", "GLOBAL")
        }
    }

    triggers {
        vcs {
            branchFilter = "+:<default>"
        }
    }
})
