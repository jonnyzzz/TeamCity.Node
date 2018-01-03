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

    params {
        text("system.path.macro.TeamCityDistribution", "%teamcity.build.checkoutDir%/%teamcity.dist%", display = ParameterDisplay.HIDDEN, allowEmpty = false)
        text("teamcity.dist", "dist/teamcity", display = ParameterDisplay.HIDDEN, allowEmpty = false)
    }

    vcs {
        root(TeamCityNodePlugin.vcsRoots.git___github_com_jonnyzzz_TeamCity_Node_git)

        checkoutMode = CheckoutMode.ON_SERVER
    }

    steps {
        step {
            type = "kotlinc"
            param("KOTLIN_TAG", "1.0.2")
        }
        ideaRunner {
            pathToProject = ""
            jdk {
                name = "1.6"
                path = "%env.JDK_16%"
                patterns("jre/lib/*.jar")
                extAnnotationPatterns("%teamcity.tool.idea%/lib/jdkAnnotations.jar")
            }
            pathvars {
                variable("TeamCityDistribution", "%system.path.macro.TeamCityDistribution%")
            }
            jvmArgs = "-Xmx256m"
            targetJdkHome = "%env.JDK_18_x64%"
            runConfigurations = "All Tests"
            artifactsToBuild = "plugin-zip"
        }
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

    dependencies {
        dependency(TeamCityNodePlugin.buildTypes.TeamCityNodePlugin_TeamCityNodeVs100x) {
            snapshot {
                reuseBuilds = ReuseBuilds.ANY
                onDependencyCancel = FailureAction.ADD_PROBLEM
            }
        }
        dependency(TeamCityNodePlugin.buildTypes.TeamCityNodePlugin_TeamCityNodeVs90x) {
            snapshot {
                reuseBuilds = ReuseBuilds.ANY
                onDependencyCancel = FailureAction.ADD_PROBLEM
            }
        }
        dependency(TeamCityNodePlugin.buildTypes.TeamCityNodePlugin_TeamCityNodeVs91x) {
            snapshot {
                reuseBuilds = ReuseBuilds.ANY
                onDependencyCancel = FailureAction.ADD_PROBLEM
            }
        }
        artifacts("JetBrainsDependencyRetrieve_TeamCityTrunkEapReleases") {
            buildRule = lastSuccessful()
            cleanDestination = true
            artifactRules = "TeamCity-*.tar.gz!TeamCity/** => %teamcity.dist%"
        }
    }
})
