package TeamCityNodePlugin.patches.buildTypes

import jetbrains.buildServer.configs.kotlin.v2017_2.*
import jetbrains.buildServer.configs.kotlin.v2017_2.ui.*

/*
This patch script was generated by TeamCity on settings change in UI.
To apply the patch, change the buildType with uuid = '576e3f87-4778-4e9f-8b27-f351ac47dc1d' (id = 'TeamCityNodePlugin_TeamCityNodeGradle')
accordingly and delete the patch script.
*/
changeBuildType("576e3f87-4778-4e9f-8b27-f351ac47dc1d") {
    dependencies {
        remove("TeamCityNodePlugin_TeamCityNodeVs100x") {
            snapshot {
                reuseBuilds = ReuseBuilds.ANY
                onDependencyCancel = FailureAction.ADD_PROBLEM
            }
        }

        remove("TeamCityNodePlugin_TeamCityNodeVs90x") {
            snapshot {
                reuseBuilds = ReuseBuilds.ANY
                onDependencyCancel = FailureAction.ADD_PROBLEM
            }
        }

        remove("TeamCityNodePlugin_TeamCityNodeVs91x") {
            snapshot {
                reuseBuilds = ReuseBuilds.ANY
                onDependencyCancel = FailureAction.ADD_PROBLEM
            }
        }

        remove("JetBrainsDependencyRetrieve_TeamCityTrunkEapReleases") {
            artifacts {
                buildRule = lastSuccessful()
                cleanDestination = true
                artifactRules = "TeamCity-*.tar.gz!TeamCity/** => %teamcity.dist%"
            }
        }

    }
}
