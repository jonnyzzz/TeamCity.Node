package TeamCityNodePlugin

import TeamCityNodePlugin.buildTypes.*
import TeamCityNodePlugin.vcsRoots.*
import TeamCityNodePlugin.vcsRoots.git___github_com_jonnyzzz_TeamCity_Node_git
import jetbrains.buildServer.configs.kotlin.v2017_2.*
import jetbrains.buildServer.configs.kotlin.v2017_2.Project
import jetbrains.buildServer.configs.kotlin.v2017_2.projectFeatures.VersionedSettings
import jetbrains.buildServer.configs.kotlin.v2017_2.projectFeatures.versionedSettings

object Project : Project({
    uuid = "8cc49a8d-a0fa-41a2-b50a-c400e4eb6bc2"
    id = "TeamCityNodePlugin"
    parentId = "TeamCityThirdPartyPlugins"
    name = "TeamCity.Node Plugin"
    description = "node.js, NPM and Phantom.JS support https://github.com/jonnyzzz/TeamCity.Node"

    vcsRoot(git___github_com_jonnyzzz_TeamCity_Node_git)

    buildType(TeamCityNodePlugin_TeamCityNodeVs90x)
    buildType(TeamCityNodePlugin_TeamCityNodeGradle)
    buildType(TeamCityNodePlugin_TeamCityNodeVs100x)
    buildType(bt434)
    buildType(TeamCityNodePlugin_TeamCityNodeVs91x)

    features {
        versionedSettings {
            id = "PROJECT_EXT_109"
            mode = VersionedSettings.Mode.ENABLED
            buildSettingsMode = VersionedSettings.BuildSettingsMode.PREFER_SETTINGS_FROM_VCS
            rootExtId = git___github_com_jonnyzzz_TeamCity_Node_git.id
            showChanges = false
            settingsFormat = VersionedSettings.Format.KOTLIN
        }
    }
})
