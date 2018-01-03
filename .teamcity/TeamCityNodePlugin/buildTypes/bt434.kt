package TeamCityNodePlugin.buildTypes

import jetbrains.buildServer.configs.kotlin.v2017_2.*
import jetbrains.buildServer.configs.kotlin.v2017_2.ideaRunner
import jetbrains.buildServer.configs.kotlin.v2017_2.triggers.vcs

object bt434 : BuildType({
    uuid = "0c137359-ef2c-4510-8196-cd5926d6ee0b"
    id = "bt434"
    name = "TeamCity.Node vs Trunk"

    artifactRules = "out/artifacts/plugin_zip/*.zip => ."
    buildNumberPattern = "1.0.%build.counter%"

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
            targetJdkHome = "%env.JDK_18_x64%"
            runConfigurations = "All Tests"
            artifactsToBuild = "plugin-zip"
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
