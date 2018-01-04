package TeamCityNodePlugin.buildTypes

import jetbrains.buildServer.configs.kotlin.v2017_2.*
import jetbrains.buildServer.configs.kotlin.v2017_2.triggers.vcs

object BuildNumber : BuildType({
    uuid = "buildnumber_56017bf7-a20d-4e46-a917-a27c909d6b5a"
    id = "TeamCityNodePlugin_TeamCityNode_BuildNumber"
    name = "Build Number"
    buildNumberPattern = "2.0.%build.counter%"

    inheritVCS()
})

object TeamCityNodePlugin_TeamCityNodeVs90x : NodeBuildType("9.0.5", {
    uuid = "56017bf7-a20d-4e46-a917-a27c909d6b5a"
    id = "TeamCityNodePlugin_TeamCityNodeVs90x"
})

object TeamCityNodePlugin_TeamCityNodeVs91x : NodeBuildType("9.1.6", {
    uuid = "34708ac2-1d6f-4bcc-b3d0-9591ba63c50a"
    id = "TeamCityNodePlugin_TeamCityNodeVs91x"
})

object TeamCityNodePlugin_TeamCityNodeVs100x : NodeBuildType("10.0.5", {
    uuid = "63558112-92ee-41d6-a7f7-827c633c5325"
    id = "TeamCityNodePlugin_TeamCityNodeVs100x"
})

object TeamCityNodePlugin_TeamCityNodeVs2017_1_x : NodeBuildType("2017.1.5", {
    uuid = "2017_1_63558112-92ee-41d6-a7f7-827c633c5325"
    id = "TeamCityNodePlugin_TeamCityNodeVs2017_1x"
})

object TeamCityNodePlugin_TeamCityNodeVs2017_2_x : NodeBuildType("2017.2", {
    uuid = "2017_2_63558112-92ee-41d6-a7f7-827c633c5325"
    id = "TeamCityNodePlugin_TeamCityNodeVs2017_2x"
})

object bt434 : BuildType({
    uuid = "0c137359-ef2c-4510-8196-cd5926d6ee0b"
    id = "bt434"
    name = "TeamCity.Node Build"

    inheritBuildNumber()

    inheritVCS()

    triggers {
        vcs {
            branchFilter = "+:<default>"
        }
    }

    dependsOn(TeamCityNodePlugin_TeamCityNodeVs2017_2_x, rePublish = true)
    dependsOn(TeamCityNodePlugin_TeamCityNodeVs2017_1_x)
    dependsOn(TeamCityNodePlugin_TeamCityNodeVs100x)
    dependsOn(TeamCityNodePlugin_TeamCityNodeVs90x)
    dependsOn(TeamCityNodePlugin_TeamCityNodeVs91x)
})

private fun BuildType.dependsOn(T: NodeBuildType, rePublish: Boolean = false) {
    val build = this

    dependencies {
        dependency(T.id) {
            snapshot {
                reuseBuilds = ReuseBuilds.ANY
                onDependencyCancel = FailureAction.ADD_PROBLEM
            }

            if (rePublish) {
                val target = "re-publish-${T.safeVersion}"
                artifacts {
                    cleanDestination = false
                    artifactRules = "+:**/* => $target"
                }

                build.artifactRules += "\n$target/** => ."
            }
        }

    }
}

fun BuildType.inheritBuildNumber() {
    buildNumberPattern = "%dep.${BuildNumber.id}.build.number%"

    dependencies {
        snapshot(BuildNumber.id) {
            reuseBuilds = ReuseBuilds.ANY
            onDependencyCancel = FailureAction.ADD_PROBLEM
        }
    }
}

