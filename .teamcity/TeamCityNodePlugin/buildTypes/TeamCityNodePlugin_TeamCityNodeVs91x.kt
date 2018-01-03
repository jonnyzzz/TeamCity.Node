package TeamCityNodePlugin.buildTypes

import jetbrains.buildServer.configs.kotlin.v2017_2.*
import jetbrains.buildServer.configs.kotlin.v2017_2.ideaRunner

object TeamCityNodePlugin_TeamCityNodeVs91x : BuildType({
    uuid = "34708ac2-1d6f-4bcc-b3d0-9591ba63c50a"
    id = "TeamCityNodePlugin_TeamCityNodeVs91x"
    name = "TeamCity.Node vs 9.1.x"

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

    dependencies {
        artifacts("JetBrainsDependencyRetrieve_TeamCity91x") {
            buildRule = lastSuccessful()
            cleanDestination = true
            artifactRules = "TeamCity-*.tar.gz!TeamCity/** => %teamcity.dist%"
        }
    }
})
